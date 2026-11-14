package com.continuum.control.model;

import com.continuum.control.model.enums.ImpactoIntervencao;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "intervencoes_temporais")
public class IntervencaoTemporal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "missao_id", nullable = false)
    private Missao missao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agente_id", nullable = false)
    private Agente agente;

    @Column(nullable = false)
    private LocalDateTime instante;

    @Column(nullable = false, length = 2000)
    private String acao;

    @Column(nullable = false, length = 2000)
    private String justificativa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ImpactoIntervencao impactoEstimado;

    @Column(nullable = false, length = 2000)
    private String resultadoObservado;

    public Long getId() { return id; }
    public Missao getMissao() { return missao; }
    public void setMissao(Missao missao) { this.missao = missao; }
    public Agente getAgente() { return agente; }
    public void setAgente(Agente agente) { this.agente = agente; }
    public LocalDateTime getInstante() { return instante; }
    public void setInstante(LocalDateTime instante) { this.instante = instante; }
    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }
    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
    public ImpactoIntervencao getImpactoEstimado() { return impactoEstimado; }
    public void setImpactoEstimado(ImpactoIntervencao impactoEstimado) { this.impactoEstimado = impactoEstimado; }
    public String getResultadoObservado() { return resultadoObservado; }
    public void setResultadoObservado(String resultadoObservado) { this.resultadoObservado = resultadoObservado; }
}
