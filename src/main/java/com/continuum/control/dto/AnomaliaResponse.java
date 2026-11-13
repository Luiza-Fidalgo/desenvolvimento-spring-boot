package com.continuum.control.dto;

import com.continuum.control.model.Anomalia;
import com.continuum.control.model.enums.RiscoAnomalia;
import com.continuum.control.model.enums.SituacaoAnomalia;
import java.time.LocalDateTime;

public class AnomaliaResponse {

    private Long id;
    private Long eventoId;
    private String descricao;
    private LocalDateTime dataDeteccao;
    private RiscoAnomalia risco;
    private SituacaoAnomalia situacao;
    private String origem;
    private String observacoes;

    public AnomaliaResponse(Anomalia anomalia) {
        this.id = anomalia.getId();
        this.eventoId = anomalia.getEvento().getId();
        this.descricao = anomalia.getDescricao();
        this.dataDeteccao = anomalia.getDataDeteccao();
        this.risco = anomalia.getRisco();
        this.situacao = anomalia.getSituacao();
        this.origem = anomalia.getOrigem();
        this.observacoes = anomalia.getObservacoes();
    }

    public Long getId() {
        return id;
    }

    public Long getEventoId() {
        return eventoId;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDateTime getDataDeteccao() {
        return dataDeteccao;
    }

    public RiscoAnomalia getRisco() {
        return risco;
    }

    public SituacaoAnomalia getSituacao() {
        return situacao;
    }

    public String getOrigem() {
        return origem;
    }

    public String getObservacoes() {
        return observacoes;
    }
}
