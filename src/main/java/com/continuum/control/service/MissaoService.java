package com.continuum.control.service;

import com.continuum.control.dto.MissaoRequest;
import com.continuum.control.dto.MissaoResponse;
import com.continuum.control.model.Agente;
import com.continuum.control.model.Anomalia;
import com.continuum.control.model.Missao;
import com.continuum.control.model.enums.SituacaoAgente;
import com.continuum.control.model.enums.SituacaoAnomalia;
import com.continuum.control.model.enums.SituacaoMissao;
import com.continuum.control.model.enums.TipoMissao;
import com.continuum.control.repository.AgenteRepository;
import com.continuum.control.repository.AnomaliaRepository;
import com.continuum.control.repository.MissaoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class MissaoService {

    private final MissaoRepository repository;
    private final AnomaliaRepository anomaliaRepository;
    private final AgenteRepository agenteRepository;

    public MissaoService(MissaoRepository repository, AnomaliaRepository anomaliaRepository,
                         AgenteRepository agenteRepository) {
        this.repository = repository;
        this.anomaliaRepository = anomaliaRepository;
        this.agenteRepository = agenteRepository;
    }

    public MissaoResponse cadastrar(MissaoRequest dados) {
        if (repository.existsByCodigo(dados.getCodigo().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Codigo de missao ja cadastrado");
        }

        Missao missao = new Missao();
        preencher(missao, dados);
        return new MissaoResponse(repository.save(missao));
    }

    public List<MissaoResponse> listar() {
        return repository.findAll().stream().map(MissaoResponse::new).toList();
    }

    public MissaoResponse buscar(Long id) {
        return new MissaoResponse(buscarEntidade(id));
    }

    public List<MissaoResponse> listarPorSituacao(SituacaoMissao situacao) {
        return repository.findBySituacao(situacao).stream().map(MissaoResponse::new).toList();
    }

    public List<MissaoResponse> listarPorAnomalia(Long anomaliaId) {
        return repository.findByAnomaliaId(anomaliaId).stream().map(MissaoResponse::new).toList();
    }

    public MissaoResponse atualizar(Long id, MissaoRequest dados) {
        Missao missao = buscarEntidade(id);
        if (missao.getSituacao() != SituacaoMissao.PLANEJADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Somente missoes planejadas podem ser alteradas");
        }
        if (!missao.getCodigo().equals(dados.getCodigo().trim()) &&
                repository.existsByCodigo(dados.getCodigo().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Codigo de missao ja cadastrado");
        }
        preencher(missao, dados);
        return new MissaoResponse(repository.save(missao));
    }

    public MissaoResponse cancelar(Long id) {
        Missao missao = buscarEntidade(id);
        if (missao.getSituacao() != SituacaoMissao.PLANEJADA &&
                missao.getSituacao() != SituacaoMissao.AUTORIZADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta missao nao pode ser cancelada");
        }
        missao.setSituacao(SituacaoMissao.CANCELADA);
        return new MissaoResponse(repository.save(missao));
    }

    private Missao buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Missao nao encontrada"));
    }

    private void preencher(Missao missao, MissaoRequest dados) {
        Anomalia anomalia = anomaliaRepository.findById(dados.getAnomaliaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anomalia nao encontrada"));

        if (dados.getTipo() == TipoMissao.CORRECAO &&
                anomalia.getSituacao() != SituacaoAnomalia.CONFIRMADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Uma missao de correcao precisa de uma anomalia confirmada");
        }

        Set<Agente> agentes = new HashSet<>();
        if (dados.getAgentesIds() != null) {
            for (Long agenteId : dados.getAgentesIds()) {
                Agente agente = agenteRepository.findById(agenteId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agente nao encontrado"));
                if (agente.getSituacao() != SituacaoAgente.DISPONIVEL) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Agente nao esta disponivel");
                }
                agentes.add(agente);
            }
        }

        missao.setCodigo(dados.getCodigo().trim());
        missao.setObjetivo(dados.getObjetivo().trim());
        missao.setAnomalia(anomalia);
        missao.setDestinoTemporal(dados.getDestinoTemporal());
        missao.setTipo(dados.getTipo());
        missao.setPrioridade(dados.getPrioridade());
        missao.setAgentes(agentes);
    }
}
