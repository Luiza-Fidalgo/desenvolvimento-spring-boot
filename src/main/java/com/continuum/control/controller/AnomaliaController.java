package com.continuum.control.controller;

import com.continuum.control.dto.AnomaliaRequest;
import com.continuum.control.dto.AnomaliaResponse;
import com.continuum.control.model.enums.RiscoAnomalia;
import com.continuum.control.model.enums.SituacaoAnomalia;
import com.continuum.control.service.AnomaliaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/anomalias")
public class AnomaliaController {

    private final AnomaliaService service;

    public AnomaliaController(AnomaliaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AnomaliaResponse> cadastrar(@Valid @RequestBody AnomaliaRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrar(dados));
    }

    @GetMapping
    public List<AnomaliaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/risco/{risco}")
    public List<AnomaliaResponse> listarPorRisco(@PathVariable RiscoAnomalia risco) {
        return service.listarPorRisco(risco);
    }

    @GetMapping("/situacao/{situacao}")
    public List<AnomaliaResponse> listarPorSituacao(@PathVariable SituacaoAnomalia situacao) {
        return service.listarPorSituacao(situacao);
    }

    @GetMapping("/evento/{eventoId}")
    public List<AnomaliaResponse> listarPorEvento(@PathVariable Long eventoId) {
        return service.listarPorEvento(eventoId);
    }

    @GetMapping("/{id}")
    public AnomaliaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    public AnomaliaResponse atualizar(@PathVariable Long id, @Valid @RequestBody AnomaliaRequest dados) {
        return service.atualizar(id, dados);
    }

    @PatchMapping("/{id}/analisar")
    public AnomaliaResponse iniciarAnalise(@PathVariable Long id) {
        return service.iniciarAnalise(id);
    }

    @PatchMapping("/{id}/confirmar")
    public AnomaliaResponse confirmar(@PathVariable Long id) {
        return service.confirmar(id);
    }

    @PatchMapping("/{id}/irreversivel")
    public AnomaliaResponse marcarIrreversivel(@PathVariable Long id) {
        return service.marcarIrreversivel(id);
    }
}
