package com.continuum.control.service;

import com.continuum.control.dto.AgenteRequest;
import com.continuum.control.model.Agente;
import com.continuum.control.model.enums.SituacaoAgente;
import com.continuum.control.repository.AgenteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AgenteService {

    private final AgenteRepository repository;

    public AgenteService(AgenteRepository repository) {
        this.repository = repository;
    }

    public Agente cadastrar(AgenteRequest dados) {
        String codigo = dados.getCodigo().trim();
        if (repository.existsByCodigo(codigo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Codigo de agente ja cadastrado");
        }

        Agente agente = new Agente();
        agente.setCodigo(codigo);
        agente.setNome(dados.getNome().trim());
        agente.setEspecialidade(dados.getEspecialidade().trim());
        return repository.save(agente);
    }

    public List<Agente> listar() {
        return repository.findAll();
    }

    public Agente buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agente nao encontrado"));
    }

    public List<Agente> listarPorSituacao(SituacaoAgente situacao) {
        return repository.findBySituacao(situacao);
    }

    public List<Agente> listarPorEspecialidade(String especialidade) {
        return repository.findByEspecialidadeIgnoreCase(especialidade);
    }

    public Agente atualizar(Long id, AgenteRequest dados) {
        Agente agente = buscar(id);
        String codigo = dados.getCodigo().trim();
        if (!agente.getCodigo().equals(codigo) && repository.existsByCodigo(codigo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Codigo de agente ja cadastrado");
        }

        agente.setCodigo(codigo);
        agente.setNome(dados.getNome().trim());
        agente.setEspecialidade(dados.getEspecialidade().trim());
        return repository.save(agente);
    }

    public Agente inativar(Long id) {
        Agente agente = buscar(id);
        if (agente.getSituacao() == SituacaoAgente.EM_MISSAO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Agente em missao nao pode ser inativado");
        }
        agente.setSituacao(SituacaoAgente.INATIVO);
        return repository.save(agente);
    }
}
