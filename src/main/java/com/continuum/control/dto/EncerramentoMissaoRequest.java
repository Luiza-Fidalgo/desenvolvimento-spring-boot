package com.continuum.control.dto;

import com.continuum.control.model.enums.SituacaoAnomalia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EncerramentoMissaoRequest {

    @NotBlank
    @Size(max = 200)
    private String resultado;

    @NotBlank
    @Size(max = 2000)
    private String resumo;

    @Size(max = 1000)
    private String impactoObservado;

    private SituacaoAnomalia situacaoFinalAnomalia;

    @Size(max = 2000)
    private String observacoes;

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    public String getResumo() { return resumo; }
    public void setResumo(String resumo) { this.resumo = resumo; }
    public String getImpactoObservado() { return impactoObservado; }
    public void setImpactoObservado(String impactoObservado) { this.impactoObservado = impactoObservado; }
    public SituacaoAnomalia getSituacaoFinalAnomalia() { return situacaoFinalAnomalia; }
    public void setSituacaoFinalAnomalia(SituacaoAnomalia situacaoFinalAnomalia) { this.situacaoFinalAnomalia = situacaoFinalAnomalia; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
