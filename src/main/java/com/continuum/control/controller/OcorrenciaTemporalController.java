package com.continuum.control.controller;

import com.continuum.control.dto.HistoricoItemResponse;
import com.continuum.control.dto.OcorrenciaRequest;
import com.continuum.control.dto.OcorrenciaResponse;
import com.continuum.control.service.HistoricoAnomaliaService;
import com.continuum.control.service.OcorrenciaTemporalService;
import com.continuum.control.service.PermissaoAgenteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OcorrenciaTemporalController {

    private final OcorrenciaTemporalService ocorrenciaService;
    private final HistoricoAnomaliaService historicoService;
    private final PermissaoAgenteService permissaoService;

    public OcorrenciaTemporalController(OcorrenciaTemporalService ocorrenciaService,
                                        HistoricoAnomaliaService historicoService,
                                        PermissaoAgenteService permissaoService) {
        this.ocorrenciaService = ocorrenciaService;
        this.historicoService = historicoService;
        this.permissaoService = permissaoService;
    }

    @PostMapping("/missoes/{missaoId}/ocorrencias")
    public ResponseEntity<OcorrenciaResponse> registrar(@PathVariable Long missaoId,
                                                          @Valid @RequestBody OcorrenciaRequest dados,
                                                          Authentication usuario) {
        permissaoService.verificarRegistro(dados.getAgenteId(), usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(ocorrenciaService.registrar(missaoId, dados));
    }

    @GetMapping("/missoes/{missaoId}/ocorrencias")
    public List<OcorrenciaResponse> listarPorMissao(@PathVariable Long missaoId) {
        return ocorrenciaService.listarPorMissao(missaoId);
    }

    @GetMapping("/anomalias/{anomaliaId}/ocorrencias")
    public List<OcorrenciaResponse> listarPorAnomalia(@PathVariable Long anomaliaId) {
        return ocorrenciaService.listarPorAnomalia(anomaliaId);
    }

    @GetMapping("/anomalias/{anomaliaId}/historico")
    public List<HistoricoItemResponse> consultarHistorico(@PathVariable Long anomaliaId) {
        return historicoService.buscar(anomaliaId);
    }
}
