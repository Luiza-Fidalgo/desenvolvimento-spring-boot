package com.continuum.control.service;

import com.continuum.control.dto.AnomaliaRequest;
import com.continuum.control.dto.AnomaliaResponse;
import com.continuum.control.model.Anomalia;
import com.continuum.control.model.EventoHistorico;
import com.continuum.control.model.enums.RiscoAnomalia;
import com.continuum.control.model.enums.SituacaoAnomalia;
import com.continuum.control.repository.AnomaliaRepository;
import com.continuum.control.repository.EventoHistoricoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class AnomaliaService {

    private final AnomaliaRepository repository;
    private final EventoHistoricoRepository eventoRepository;

    public AnomaliaService(AnomaliaRepository repository, EventoHistoricoRepository eventoRepository) {
        this.repository = repository;
        this.eventoRepository = eventoRepository;
    }

    public AnomaliaResponse cadastrar(AnomaliaRequest dados) {
        Anomalia anomalia = new Anomalia();
        preencher(anomalia, dados);
        return new AnomaliaResponse(repository.save(anomalia));
    }

    public List<AnomaliaResponse> listar() {
        return converter(repository.findAll());
    }

    public AnomaliaResponse buscar(Long id) {
        return new AnomaliaResponse(buscarEntidade(id));
    }

    public List<AnomaliaResponse> listarPorRisco(RiscoAnomalia risco) {
        return converter(repository.findByRisco(risco));
    }

    public List<AnomaliaResponse> listarPorSituacao(SituacaoAnomalia situacao) {
        return converter(repository.findBySituacao(situacao));
    }

    public List<AnomaliaResponse> listarPorEvento(Long eventoId) {
        return converter(repository.findByEventoId(eventoId));
    }

    public AnomaliaResponse atualizar(Long id, AnomaliaRequest dados) {
        Anomalia anomalia = buscarEntidade(id);
        if (anomalia.getSituacao() == SituacaoAnomalia.ESTABILIZADA ||
                anomalia.getSituacao() == SituacaoAnomalia.IRREVERSIVEL) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Anomalia encerrada nao pode ser alterada");
        }
        preencher(anomalia, dados);
        return new AnomaliaResponse(repository.save(anomalia));
    }

    public AnomaliaResponse iniciarAnalise(Long id) {
        return alterarSituacao(id, SituacaoAnomalia.DETECTADA, SituacaoAnomalia.EM_ANALISE);
    }

    public AnomaliaResponse confirmar(Long id) {
        return alterarSituacao(id, SituacaoAnomalia.EM_ANALISE, SituacaoAnomalia.CONFIRMADA);
    }

    public AnomaliaResponse marcarIrreversivel(Long id) {
        Anomalia anomalia = buscarEntidade(id);
        if (anomalia.getSituacao() != SituacaoAnomalia.EM_ANALISE &&
                anomalia.getSituacao() != SituacaoAnomalia.CONFIRMADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Situacao nao permite marcar como irreversivel");
        }
        anomalia.setSituacao(SituacaoAnomalia.IRREVERSIVEL);
        return new AnomaliaResponse(repository.save(anomalia));
    }

    private AnomaliaResponse alterarSituacao(Long id, SituacaoAnomalia atual, SituacaoAnomalia proxima) {
        Anomalia anomalia = buscarEntidade(id);
        if (anomalia.getSituacao() != atual) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Situacao nao permite esta operacao");
        }
        anomalia.setSituacao(proxima);
        return new AnomaliaResponse(repository.save(anomalia));
    }

    private Anomalia buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anomalia nao encontrada"));
    }

    private void preencher(Anomalia anomalia, AnomaliaRequest dados) {
        EventoHistorico evento = eventoRepository.findById(dados.getEventoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento nao encontrado"));
        anomalia.setEvento(evento);
        anomalia.setDescricao(dados.getDescricao().trim());
        anomalia.setDataDeteccao(dados.getDataDeteccao());
        anomalia.setRisco(dados.getRisco());
        anomalia.setOrigem(dados.getOrigem() == null ? null : dados.getOrigem().trim());
        anomalia.setObservacoes(dados.getObservacoes() == null ? null : dados.getObservacoes().trim());
    }

    private List<AnomaliaResponse> converter(List<Anomalia> anomalias) {
        return anomalias.stream().map(AnomaliaResponse::new).toList();
    }
}
