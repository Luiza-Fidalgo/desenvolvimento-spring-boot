package com.continuum.control.service;

import com.continuum.control.dto.IntervencaoRequest;
import com.continuum.control.dto.IntervencaoResponse;
import com.continuum.control.model.Agente;
import com.continuum.control.model.IntervencaoTemporal;
import com.continuum.control.model.Missao;
import com.continuum.control.model.enums.SituacaoMissao;
import com.continuum.control.repository.AnomaliaRepository;
import com.continuum.control.repository.IntervencaoTemporalRepository;
import com.continuum.control.repository.MissaoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class IntervencaoTemporalService {
    private final IntervencaoTemporalRepository repository;
    private final MissaoRepository missaoRepository;
    private final AnomaliaRepository anomaliaRepository;

    public IntervencaoTemporalService(IntervencaoTemporalRepository repository,
                                       MissaoRepository missaoRepository,
                                       AnomaliaRepository anomaliaRepository) {
        this.repository = repository;
        this.missaoRepository = missaoRepository;
        this.anomaliaRepository = anomaliaRepository;
    }

    @Transactional
    public IntervencaoResponse registrar(Long missaoId, IntervencaoRequest dados) {
        Missao missao = missaoRepository.findById(missaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Missao nao encontrada"));
        if (missao.getSituacao() != SituacaoMissao.EM_EXECUCAO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Missao precisa estar em execucao");
        }
        Agente agente = missao.getAgentes().stream()
                .filter(a -> Objects.equals(a.getId(), dados.getAgenteId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                        "Agente nao participa desta missao"));

        IntervencaoTemporal intervencao = new IntervencaoTemporal();
        intervencao.setMissao(missao);
        intervencao.setAgente(agente);
        intervencao.setInstante(LocalDateTime.now());
        intervencao.setAcao(dados.getAcao().trim());
        intervencao.setJustificativa(dados.getJustificativa().trim());
        intervencao.setImpactoEstimado(dados.getImpactoEstimado());
        intervencao.setResultadoObservado(dados.getResultadoObservado().trim());
        return new IntervencaoResponse(repository.save(intervencao));
    }

    @Transactional(readOnly = true)
    public List<IntervencaoResponse> listarPorMissao(Long missaoId) {
        if (!missaoRepository.existsById(missaoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Missao nao encontrada");
        }
        return repository.findByMissaoIdOrderByInstanteAsc(missaoId)
                .stream().map(IntervencaoResponse::new).toList();
    }

    @Transactional(readOnly = true)
    public List<IntervencaoResponse> listarPorAnomalia(Long anomaliaId) {
        if (!anomaliaRepository.existsById(anomaliaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Anomalia nao encontrada");
        }
        return repository.findByMissao_Anomalia_IdOrderByInstanteAsc(anomaliaId)
                .stream().map(IntervencaoResponse::new).toList();
    }
}
