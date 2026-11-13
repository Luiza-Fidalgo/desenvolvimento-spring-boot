package com.continuum.control.dto;

import com.continuum.control.model.enums.PrioridadeMissao;
import com.continuum.control.model.enums.TipoMissao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MissaoRequest {

    @NotBlank
    @Size(max = 30)
    private String codigo;

    @NotBlank
    @Size(max = 1000)
    private String objetivo;

    @NotNull
    @Positive
    private Long anomaliaId;

    @NotNull
    private LocalDate destinoTemporal;

    @NotNull
    private TipoMissao tipo;

    @NotNull
    private PrioridadeMissao prioridade;

    private List<@NotNull @Positive Long> agentesIds = new ArrayList<>();

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public Long getAnomaliaId() {
        return anomaliaId;
    }

    public void setAnomaliaId(Long anomaliaId) {
        this.anomaliaId = anomaliaId;
    }

    public LocalDate getDestinoTemporal() {
        return destinoTemporal;
    }

    public void setDestinoTemporal(LocalDate destinoTemporal) {
        this.destinoTemporal = destinoTemporal;
    }

    public TipoMissao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMissao tipo) {
        this.tipo = tipo;
    }

    public PrioridadeMissao getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(PrioridadeMissao prioridade) {
        this.prioridade = prioridade;
    }

    public List<Long> getAgentesIds() {
        return agentesIds;
    }

    public void setAgentesIds(List<Long> agentesIds) {
        this.agentesIds = agentesIds;
    }
}
