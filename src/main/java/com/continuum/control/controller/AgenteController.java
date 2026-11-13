package com.continuum.control.controller;

import com.continuum.control.dto.AgenteRequest;
import com.continuum.control.model.Agente;
import com.continuum.control.model.enums.SituacaoAgente;
import com.continuum.control.service.AgenteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agentes")
public class AgenteController {

    private final AgenteService service;

    public AgenteController(AgenteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Agente> cadastrar(@Valid @RequestBody AgenteRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrar(dados));
    }

    @GetMapping
    public List<Agente> listar() {
        return service.listar();
    }

    @GetMapping("/disponiveis")
    public List<Agente> listarDisponiveis() {
        return service.listarPorSituacao(SituacaoAgente.DISPONIVEL);
    }

    @GetMapping("/situacao/{situacao}")
    public List<Agente> listarPorSituacao(@PathVariable SituacaoAgente situacao) {
        return service.listarPorSituacao(situacao);
    }

    @GetMapping("/especialidade/{especialidade}")
    public List<Agente> listarPorEspecialidade(@PathVariable String especialidade) {
        return service.listarPorEspecialidade(especialidade);
    }

    @GetMapping("/{id}")
    public Agente buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    public Agente atualizar(@PathVariable Long id, @Valid @RequestBody AgenteRequest dados) {
        return service.atualizar(id, dados);
    }

    @PatchMapping("/{id}/inativar")
    public Agente inativar(@PathVariable Long id) {
        return service.inativar(id);
    }
}
