package com.continuum.control.dto;

import com.continuum.control.model.enums.ImpactoIntervencao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class IntervencaoRequest {
    @NotNull @Positive
    private Long agenteId;

    @NotBlank @Size(max = 2000)
    private String acao;

    @NotBlank @Size(max = 2000)
    private String justificativa;

    @NotNull
    private ImpactoIntervencao impactoEstimado;

    @NotBlank @Size(max = 2000)
    private String resultadoObservado;

    public Long getAgenteId() { return agenteId; }
    public void setAgenteId(Long agenteId) { this.agenteId = agenteId; }
    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }
    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
    public ImpactoIntervencao getImpactoEstimado() { return impactoEstimado; }
    public void setImpactoEstimado(ImpactoIntervencao impactoEstimado) { this.impactoEstimado = impactoEstimado; }
    public String getResultadoObservado() { return resultadoObservado; }
    public void setResultadoObservado(String resultadoObservado) { this.resultadoObservado = resultadoObservado; }
}
