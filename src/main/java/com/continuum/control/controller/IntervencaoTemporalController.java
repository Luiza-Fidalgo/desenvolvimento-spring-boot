package com.continuum.control.controller;

import com.continuum.control.dto.IntervencaoRequest;
import com.continuum.control.dto.IntervencaoResponse;
import com.continuum.control.service.IntervencaoTemporalService;
import com.continuum.control.service.PermissaoAgenteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class IntervencaoTemporalController {
    private final IntervencaoTemporalService service;
    private final PermissaoAgenteService permissaoService;

    public IntervencaoTemporalController(IntervencaoTemporalService service,
                                         PermissaoAgenteService permissaoService) {
        this.service = service;
        this.permissaoService = permissaoService;
    }

    @PostMapping("/missoes/{missaoId}/intervencoes")
    public ResponseEntity<IntervencaoResponse> registrar(@PathVariable Long missaoId,
            @Valid @RequestBody IntervencaoRequest dados, Authentication usuario) {
        permissaoService.verificarRegistro(dados.getAgenteId(), usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(missaoId, dados));
    }

    @GetMapping("/missoes/{missaoId}/intervencoes")
    public List<IntervencaoResponse> listarPorMissao(@PathVariable Long missaoId) {
        return service.listarPorMissao(missaoId);
    }

    @GetMapping("/anomalias/{anomaliaId}/intervencoes")
    public List<IntervencaoResponse> listarPorAnomalia(@PathVariable Long anomaliaId) {
        return service.listarPorAnomalia(anomaliaId);
    }
}
