package com.continuum.control.service;

import com.continuum.control.dto.HistoricoItemResponse;
import com.continuum.control.model.Anomalia;
import com.continuum.control.model.Autorizacao;
import com.continuum.control.model.Missao;
import com.continuum.control.model.OcorrenciaTemporal;
import com.continuum.control.repository.AnomaliaRepository;
import com.continuum.control.repository.AutorizacaoRepository;
import com.continuum.control.repository.MissaoRepository;
import com.continuum.control.repository.OcorrenciaTemporalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HistoricoAnomaliaService {

    private final AnomaliaRepository anomaliaRepository;
    private final MissaoRepository missaoRepository;
    private final AutorizacaoRepository autorizacaoRepository;
    private final OcorrenciaTemporalRepository ocorrenciaRepository;

    public HistoricoAnomaliaService(AnomaliaRepository anomaliaRepository, MissaoRepository missaoRepository,
                                    AutorizacaoRepository autorizacaoRepository,
                                    OcorrenciaTemporalRepository ocorrenciaRepository) {
        this.anomaliaRepository = anomaliaRepository;
        this.missaoRepository = missaoRepository;
        this.autorizacaoRepository = autorizacaoRepository;
        this.ocorrenciaRepository = ocorrenciaRepository;
    }

    @Transactional(readOnly = true)
    public List<HistoricoItemResponse> buscar(Long anomaliaId) {
        Anomalia anomalia = anomaliaRepository.findById(anomaliaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anomalia nao encontrada"));

        List<HistoricoItemResponse> historico = new ArrayList<>();
        historico.add(new HistoricoItemResponse(anomalia.getDataDeteccao(), "DETECCAO",
                anomalia.getDescricao(), null, Map.of("risco", anomalia.getRisco().name())));

        for (Missao missao : missaoRepository.findByAnomaliaId(anomaliaId)) {
            for (Autorizacao autorizacao : autorizacaoRepository.findByMissaoId(missao.getId())) {
                Map<String, Object> dados = new HashMap<>();
                dados.put("responsavel", autorizacao.getResponsavel());
                if (autorizacao.getObservacao() != null) {
                    dados.put("observacao", autorizacao.getObservacao());
                }
                historico.add(new HistoricoItemResponse(autorizacao.getInstante(), "AUTORIZACAO",
                        "Missao autorizada", missao.getId(), dados));
            }
            if (missao.getEncerradaEm() != null) {
                Map<String, Object> dados = new HashMap<>();
                dados.put("situacao", missao.getSituacao().name());
                if (missao.getResultado() != null) { dados.put("resultado", missao.getResultado()); }
                if (missao.getSituacaoFinalAnomalia() != null) {
                    dados.put("situacaoFinalAnomalia", missao.getSituacaoFinalAnomalia().name());
                }
                historico.add(new HistoricoItemResponse(missao.getEncerradaEm(), "ENCERRAMENTO",
                        missao.getResumo(), missao.getId(), dados));
            }
        }

        for (OcorrenciaTemporal ocorrencia : ocorrenciaRepository.findByAnomaliaIdOrderByInstanteAsc(anomaliaId)) {
            historico.add(new HistoricoItemResponse(ocorrencia.getInstante(),
                    "OCORRENCIA_" + ocorrencia.getTipo().name(), ocorrencia.getDescricao(),
                    ocorrencia.getMissaoId(), ocorrencia.getDetalhes()));
        }

        historico.sort(Comparator.comparing(HistoricoItemResponse::getInstante));
        return historico;
    }
}
