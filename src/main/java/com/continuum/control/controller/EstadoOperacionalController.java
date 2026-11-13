package com.continuum.control.controller;

import com.continuum.control.dto.EstadoOperacionalResponse;
import com.continuum.control.service.EstadoOperacionalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EstadoOperacionalController {

    private final EstadoOperacionalService service;

    public EstadoOperacionalController(EstadoOperacionalService service) {
        this.service = service;
    }

    @GetMapping("/operacional/estado")
    public EstadoOperacionalResponse consultar(@RequestParam(defaultValue = "false") boolean atualizar) {
        return service.consultar(atualizar);
    }
}
