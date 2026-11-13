package com.continuum.control.service;

import com.continuum.control.dto.OcorrenciaRequest;
import com.continuum.control.dto.OcorrenciaResponse;
import com.continuum.control.model.Missao;
import com.continuum.control.model.OcorrenciaTemporal;
import com.continuum.control.model.enums.SituacaoMissao;
import com.continuum.control.repository.AnomaliaRepository;
import com.continuum.control.repository.MissaoRepository;
import com.continuum.control.repository.OcorrenciaTemporalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class OcorrenciaTemporalService {

    private final OcorrenciaTemporalRepository repository;
    private final MissaoRepository missaoRepository;
    private final AnomaliaRepository anomaliaRepository;

    public OcorrenciaTemporalService(OcorrenciaTemporalRepository repository, MissaoRepository missaoRepository,
                                     AnomaliaRepository anomaliaRepository) {
        this.repository = repository;
        this.missaoRepository = missaoRepository;
        this.anomaliaRepository = anomaliaRepository;
    }

    @Transactional
    public OcorrenciaResponse registrar(Long missaoId, OcorrenciaRequest dados) {
        Missao missao = missaoRepository.findById(missaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Missao nao encontrada"));
        if (missao.getSituacao() != SituacaoMissao.EM_EXECUCAO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Somente missoes em execucao recebem ocorrencias");
        }
        boolean participa = missao.getAgentes().stream()
                .anyMatch(agente -> Objects.equals(agente.getId(), dados.getAgenteId()));
        if (!participa) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Agente nao participa desta missao");
        }

        OcorrenciaTemporal ocorrencia = new OcorrenciaTemporal();
        ocorrencia.setMissaoId(missaoId);
        ocorrencia.setAnomaliaId(missao.getAnomalia().getId());
        ocorrencia.setAgenteId(dados.getAgenteId());
        ocorrencia.setTipo(dados.getTipo());
        ocorrencia.setDescricao(dados.getDescricao().trim());
        ocorrencia.setInstante(LocalDateTime.now());
        ocorrencia.setDetalhes(dados.getDetalhes() == null ? new HashMap<>() : new HashMap<>(dados.getDetalhes()));

        // No MongoDB o registro fica como documento; nao existe JOIN com o PostgreSQL.
        return new OcorrenciaResponse(repository.save(ocorrencia));
    }

    public List<OcorrenciaResponse> listarPorMissao(Long missaoId) {
        if (!missaoRepository.existsById(missaoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Missao nao encontrada");
        }
        return repository.findByMissaoIdOrderByInstanteAsc(missaoId)
                .stream().map(OcorrenciaResponse::new).toList();
    }

    public List<OcorrenciaResponse> listarPorAnomalia(Long anomaliaId) {
        if (!anomaliaRepository.existsById(anomaliaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Anomalia nao encontrada");
        }
        return repository.findByAnomaliaIdOrderByInstanteAsc(anomaliaId)
                .stream().map(OcorrenciaResponse::new).toList();
    }
}
