package com.continuum.control.dto;

import com.continuum.control.model.OcorrenciaTemporal;
import com.continuum.control.model.enums.TipoOcorrencia;

import java.time.LocalDateTime;
import java.util.Map;

public class OcorrenciaResponse {
    private final String id;
    private final Long missaoId;
    private final Long anomaliaId;
    private final Long agenteId;
    private final TipoOcorrencia tipo;
    private final String descricao;
    private final LocalDateTime instante;
    private final Map<String, Object> detalhes;

    public OcorrenciaResponse(OcorrenciaTemporal ocorrencia) {
        this.id = ocorrencia.getId();
        this.missaoId = ocorrencia.getMissaoId();
        this.anomaliaId = ocorrencia.getAnomaliaId();
        this.agenteId = ocorrencia.getAgenteId();
        this.tipo = ocorrencia.getTipo();
        this.descricao = ocorrencia.getDescricao();
        this.instante = ocorrencia.getInstante();
        this.detalhes = ocorrencia.getDetalhes();
    }

    public String getId() { return id; }
    public Long getMissaoId() { return missaoId; }
    public Long getAnomaliaId() { return anomaliaId; }
    public Long getAgenteId() { return agenteId; }
    public TipoOcorrencia getTipo() { return tipo; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getInstante() { return instante; }
    public Map<String, Object> getDetalhes() { return detalhes; }
}
