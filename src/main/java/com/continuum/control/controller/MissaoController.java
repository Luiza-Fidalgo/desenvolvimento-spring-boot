package com.continuum.control.controller;

import com.continuum.control.dto.MissaoRequest;
import com.continuum.control.dto.MissaoResponse;
import com.continuum.control.model.enums.SituacaoMissao;
import com.continuum.control.service.MissaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/missoes")
public class MissaoController {

    private final MissaoService service;

    public MissaoController(MissaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MissaoResponse> cadastrar(@Valid @RequestBody MissaoRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrar(dados));
    }

    @GetMapping
    public List<MissaoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/situacao/{situacao}")
    public List<MissaoResponse> listarPorSituacao(@PathVariable SituacaoMissao situacao) {
        return service.listarPorSituacao(situacao);
    }

    @GetMapping("/anomalia/{anomaliaId}")
    public List<MissaoResponse> listarPorAnomalia(@PathVariable Long anomaliaId) {
        return service.listarPorAnomalia(anomaliaId);
    }

    @GetMapping("/{id}")
    public MissaoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    public MissaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody MissaoRequest dados) {
        return service.atualizar(id, dados);
    }

    @PatchMapping("/{id}/cancelar")
    public MissaoResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }
}
