package com.continuum.control.dto;

import com.continuum.control.model.Autorizacao;
import java.time.LocalDateTime;

public class AutorizacaoResponse {

    private final Long id;
    private final Long missaoId;
    private final String responsavel;
    private final LocalDateTime instante;
    private final String observacao;

    public AutorizacaoResponse(Autorizacao autorizacao) {
        this.id = autorizacao.getId();
        this.missaoId = autorizacao.getMissao().getId();
        this.responsavel = autorizacao.getResponsavel();
        this.instante = autorizacao.getInstante();
        this.observacao = autorizacao.getObservacao();
    }

    public Long getId() { return id; }
    public Long getMissaoId() { return missaoId; }
    public String getResponsavel() { return responsavel; }
    public LocalDateTime getInstante() { return instante; }
    public String getObservacao() { return observacao; }
}
