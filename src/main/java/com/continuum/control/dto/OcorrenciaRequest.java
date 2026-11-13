package com.continuum.control.dto;

import com.continuum.control.model.enums.TipoOcorrencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.HashMap;
import java.util.Map;

public class OcorrenciaRequest {

    @NotNull
    @Positive
    private Long agenteId;

    @NotNull
    private TipoOcorrencia tipo;

    @NotBlank
    @Size(max = 2000)
    private String descricao;

    // Exemplos: intensidade e duracao, ou objeto e confiabilidade.
    private Map<String, Object> detalhes = new HashMap<>();

    public Long getAgenteId() { return agenteId; }
    public void setAgenteId(Long agenteId) { this.agenteId = agenteId; }
    public TipoOcorrencia getTipo() { return tipo; }
    public void setTipo(TipoOcorrencia tipo) { this.tipo = tipo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Map<String, Object> getDetalhes() { return detalhes; }
    public void setDetalhes(Map<String, Object> detalhes) { this.detalhes = detalhes; }
}
