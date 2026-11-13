package com.continuum.control.controller;

import com.continuum.control.dto.EventoHistoricoRequest;
import com.continuum.control.model.EventoHistorico;
import com.continuum.control.model.enums.ImportanciaEvento;
import com.continuum.control.service.EventoHistoricoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoHistoricoController {

    private final EventoHistoricoService service;

    public EventoHistoricoController(EventoHistoricoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EventoHistorico> cadastrar(@Valid @RequestBody EventoHistoricoRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrar(dados));
    }

    @GetMapping
    public List<EventoHistorico> listar() {
        return service.listar();
    }

    @GetMapping("/importancia/{importancia}")
    public List<EventoHistorico> listarPorImportancia(@PathVariable ImportanciaEvento importancia) {
        return service.listarPorImportancia(importancia);
    }

    @GetMapping("/periodo")
    public List<EventoHistorico> listarPorPeriodo(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam("fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return service.listarPorPeriodo(inicio, fim);
    }

    @GetMapping("/{id}")
    public EventoHistorico buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    public EventoHistorico atualizar(@PathVariable Long id, @Valid @RequestBody EventoHistoricoRequest dados) {
        return service.atualizar(id, dados);
    }
}
