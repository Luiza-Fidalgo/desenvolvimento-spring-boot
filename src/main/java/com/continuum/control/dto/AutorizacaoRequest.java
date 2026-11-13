package com.continuum.control.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AutorizacaoRequest {

    @NotBlank
    @Size(max = 100)
    private String responsavel;

    @Size(max = 1000)
    private String observacao;

    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
