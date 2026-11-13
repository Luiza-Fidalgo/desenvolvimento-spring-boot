package com.continuum.control.dto;

import com.continuum.control.model.enums.RiscoAnomalia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class AnomaliaRequest {

    @NotNull(message = "Evento e obrigatorio")
    private Long eventoId;

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 2000)
    private String descricao;

    @NotNull(message = "Data da deteccao e obrigatoria")
    private LocalDateTime dataDeteccao;

    @NotNull(message = "Risco e obrigatorio")
    private RiscoAnomalia risco;

    @Size(max = 200)
    private String origem;

    @Size(max = 2000)
    private String observacoes;

    public Long getEventoId() {
        return eventoId;
    }

    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataDeteccao() {
        return dataDeteccao;
    }

    public void setDataDeteccao(LocalDateTime dataDeteccao) {
        this.dataDeteccao = dataDeteccao;
    }

    public RiscoAnomalia getRisco() {
        return risco;
    }

    public void setRisco(RiscoAnomalia risco) {
        this.risco = risco;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
