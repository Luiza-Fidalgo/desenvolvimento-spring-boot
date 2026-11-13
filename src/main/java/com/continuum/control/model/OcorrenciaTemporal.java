package com.continuum.control.model;

import com.continuum.control.model.enums.TipoOcorrencia;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Cada ocorrencia e um documento do MongoDB, com detalhes que podem variar.
@Document(collection = "ocorrencias_temporais")
public class OcorrenciaTemporal {

    @Id
    private String id;
    private Long missaoId;
    private Long anomaliaId;
    private Long agenteId;
    private TipoOcorrencia tipo;
    private String descricao;
    private LocalDateTime instante;
    private Map<String, Object> detalhes = new HashMap<>();

    public String getId() { return id; }
    public Long getMissaoId() { return missaoId; }
    public void setMissaoId(Long missaoId) { this.missaoId = missaoId; }
    public Long getAnomaliaId() { return anomaliaId; }
    public void setAnomaliaId(Long anomaliaId) { this.anomaliaId = anomaliaId; }
    public Long getAgenteId() { return agenteId; }
    public void setAgenteId(Long agenteId) { this.agenteId = agenteId; }
    public TipoOcorrencia getTipo() { return tipo; }
    public void setTipo(TipoOcorrencia tipo) { this.tipo = tipo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDateTime getInstante() { return instante; }
    public void setInstante(LocalDateTime instante) { this.instante = instante; }
    public Map<String, Object> getDetalhes() { return detalhes; }
    public void setDetalhes(Map<String, Object> detalhes) { this.detalhes = detalhes; }
}
