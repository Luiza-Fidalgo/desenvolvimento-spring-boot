package com.continuum.control.service;

import com.continuum.control.dto.MissaoRequest;
import com.continuum.control.dto.MissaoResponse;
import com.continuum.control.dto.AutorizacaoRequest;
import com.continuum.control.dto.AutorizacaoResponse;
import com.continuum.control.dto.EncerramentoMissaoRequest;
import com.continuum.control.model.Agente;
import com.continuum.control.model.Autorizacao;
import com.continuum.control.model.Anomalia;
import com.continuum.control.model.Missao;
import com.continuum.control.model.enums.SituacaoAgente;
import com.continuum.control.model.enums.SituacaoAnomalia;
import com.continuum.control.model.enums.SituacaoMissao;
import com.continuum.control.model.enums.TipoMissao;
import com.continuum.control.repository.AgenteRepository;
import com.continuum.control.repository.AutorizacaoRepository;
import com.continuum.control.repository.AnomaliaRepository;
import com.continuum.control.repository.MissaoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class MissaoService {

    private final MissaoRepository repository;
    private final AnomaliaRepository anomaliaRepository;
    private final AgenteRepository agenteRepository;
    private final AutorizacaoRepository autorizacaoRepository;

    public MissaoService(MissaoRepository repository, AnomaliaRepository anomaliaRepository,
                         AgenteRepository agenteRepository, AutorizacaoRepository autorizacaoRepository) {
        this.repository = repository;
        this.anomaliaRepository = anomaliaRepository;
        this.agenteRepository = agenteRepository;
        this.autorizacaoRepository = autorizacaoRepository;
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

    // A autorizacao fica registrada separadamente para poder ser consultada depois.
    public MissaoResponse autorizar(Long id, AutorizacaoRequest dados) {
        Missao missao = buscarEntidade(id);
        if (missao.getSituacao() != SituacaoMissao.PLANEJADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Missao nao esta planejada");
        }
        verificarAgentesDisponiveis(missao);
        verificarCorrecao(missao);

        Autorizacao autorizacao = new Autorizacao();
        autorizacao.setMissao(missao);
        autorizacao.setResponsavel(dados.getResponsavel().trim());
        autorizacao.setObservacao(dados.getObservacao() == null ? null : dados.getObservacao().trim());
        autorizacao.setInstante(LocalDateTime.now());
        autorizacaoRepository.save(autorizacao);

        missao.setSituacao(SituacaoMissao.AUTORIZADA);
        return new MissaoResponse(repository.save(missao));
    }

    public List<AutorizacaoResponse> listarAutorizacoes(Long id) {
        buscarEntidade(id);
        return autorizacaoRepository.findByMissaoId(id)
                .stream().map(AutorizacaoResponse::new).toList();
    }

    // Missao, agentes e anomalia sao atualizados na mesma transacao.
    public MissaoResponse iniciar(Long id) {
        Missao missao = buscarEntidade(id);
        if (missao.getSituacao() != SituacaoMissao.AUTORIZADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Missao ainda nao foi autorizada");
        }
        verificarAgentesDisponiveis(missao);
        verificarCorrecao(missao);

        missao.setSituacao(SituacaoMissao.EM_EXECUCAO);
        for (Agente agente : missao.getAgentes()) {
            agente.setSituacao(SituacaoAgente.EM_MISSAO);
        }
        agenteRepository.saveAll(missao.getAgentes());

        if (missao.getTipo() == TipoMissao.CORRECAO) {
            missao.getAnomalia().setSituacao(SituacaoAnomalia.EM_CORRECAO);
            anomaliaRepository.save(missao.getAnomalia());
        }
        return new MissaoResponse(repository.save(missao));
    }

    public MissaoResponse concluir(Long id, EncerramentoMissaoRequest dados) {
        Missao missao = buscarEntidade(id);
        verificarEmExecucao(missao);

        Anomalia anomalia = missao.getAnomalia();
        if (missao.getTipo() == TipoMissao.CORRECAO) {
            if (anomalia.getSituacao() != SituacaoAnomalia.EM_CORRECAO) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Anomalia nao esta em correcao");
            }
            SituacaoAnomalia finalDesejada = dados.getSituacaoFinalAnomalia();
            if (finalDesejada != SituacaoAnomalia.ESTABILIZADA &&
                    finalDesejada != SituacaoAnomalia.IRREVERSIVEL) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Informe ESTABILIZADA ou IRREVERSIVEL para finalizar a correcao");
            }
            anomalia.setSituacao(finalDesejada);
            anomaliaRepository.save(anomalia);
        } else if (dados.getSituacaoFinalAnomalia() != null &&
                dados.getSituacaoFinalAnomalia() != anomalia.getSituacao()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A investigacao nao altera automaticamente a situacao da anomalia");
        }

        registrarEncerramento(missao, dados);
        missao.setSituacao(SituacaoMissao.CONCLUIDA);
        missao.setSituacaoFinalAnomalia(anomalia.getSituacao());
        liberarAgentes(missao);
        return new MissaoResponse(repository.save(missao));
    }

    public MissaoResponse falhar(Long id, EncerramentoMissaoRequest dados) {
        Missao missao = buscarEntidade(id);
        verificarEmExecucao(missao);

        Anomalia anomalia = missao.getAnomalia();
        if (missao.getTipo() == TipoMissao.CORRECAO &&
                anomalia.getSituacao() == SituacaoAnomalia.EM_CORRECAO) {
            // Falha nao significa que a anomalia foi resolvida.
            anomalia.setSituacao(SituacaoAnomalia.CONFIRMADA);
            anomaliaRepository.save(anomalia);
        }
        if (dados.getSituacaoFinalAnomalia() != null &&
                dados.getSituacaoFinalAnomalia() != anomalia.getSituacao()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A situacao final informada nao corresponde a anomalia");
        }

        registrarEncerramento(missao, dados);
        missao.setSituacao(SituacaoMissao.FALHOU);
        missao.setSituacaoFinalAnomalia(anomalia.getSituacao());
        liberarAgentes(missao);
        return new MissaoResponse(repository.save(missao));
    }

    private void verificarAgentesDisponiveis(Missao missao) {
        if (missao.getAgentes().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Missao precisa de pelo menos um agente");
        }
        for (Agente agente : missao.getAgentes()) {
            if (agente.getSituacao() != SituacaoAgente.DISPONIVEL) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Agente nao esta disponivel");
            }
        }
    }

    private void verificarCorrecao(Missao missao) {
        if (missao.getTipo() == TipoMissao.CORRECAO &&
                missao.getAnomalia().getSituacao() != SituacaoAnomalia.CONFIRMADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Anomalia precisa estar confirmada");
        }
    }

    private void verificarEmExecucao(Missao missao) {
        if (missao.getSituacao() != SituacaoMissao.EM_EXECUCAO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Missao nao esta em execucao");
        }
    }

    private void registrarEncerramento(Missao missao, EncerramentoMissaoRequest dados) {
        missao.setResultado(dados.getResultado().trim());
        missao.setResumo(dados.getResumo().trim());
        missao.setImpactoObservado(dados.getImpactoObservado() == null ? null : dados.getImpactoObservado().trim());
        missao.setObservacoesEncerramento(dados.getObservacoes() == null ? null : dados.getObservacoes().trim());
        missao.setEncerradaEm(LocalDateTime.now());
    }

    private void liberarAgentes(Missao missao) {
        for (Agente agente : missao.getAgentes()) {
            if (agente.getSituacao() == SituacaoAgente.EM_MISSAO) {
                agente.setSituacao(SituacaoAgente.DISPONIVEL);
            }
        }
        agenteRepository.saveAll(missao.getAgentes());
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
