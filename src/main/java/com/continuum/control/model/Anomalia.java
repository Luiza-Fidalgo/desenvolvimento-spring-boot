package com.continuum.control.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.continuum.control.model.enums.RiscoAnomalia;
import com.continuum.control.model.enums.SituacaoAnomalia;

@Entity
@Table(name = "anomalias")
public class Anomalia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private EventoHistorico evento;

    @Column(nullable = false, length = 2000)
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime dataDeteccao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiscoAnomalia risco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SituacaoAnomalia situacao = SituacaoAnomalia.DETECTADA;

    @Column(length = 200)
    private String origem;

    @Column(length = 2000)
    private String observacoes;

    public Anomalia() {
    }

    public Long getId() {
        return id;
    }

    public EventoHistorico getEvento() {
        return evento;
    }

    public void setEvento(EventoHistorico evento) {
        this.evento = evento;
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

    public SituacaoAnomalia getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoAnomalia situacao) {
        this.situacao = situacao;
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
