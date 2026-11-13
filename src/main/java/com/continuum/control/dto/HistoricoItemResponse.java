package com.continuum.control.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class HistoricoItemResponse {
    private final LocalDateTime instante;
    private final String tipo;
    private final String descricao;
    private final Long missaoId;
    private final Map<String, Object> dados;

    public HistoricoItemResponse(LocalDateTime instante, String tipo, String descricao,
                                 Long missaoId, Map<String, Object> dados) {
        this.instante = instante;
        this.tipo = tipo;
        this.descricao = descricao;
        this.missaoId = missaoId;
        this.dados = dados;
    }

    public LocalDateTime getInstante() { return instante; }
    public String getTipo() { return tipo; }
    public String getDescricao() { return descricao; }
    public Long getMissaoId() { return missaoId; }
    public Map<String, Object> getDados() { return dados; }
}
