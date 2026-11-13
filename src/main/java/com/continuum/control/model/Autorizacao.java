package com.continuum.control.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "autorizacoes")
public class Autorizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "missao_id", nullable = false)
    private Missao missao;

    @Column(nullable = false, length = 100)
    private String responsavel;

    @Column(nullable = false)
    private LocalDateTime instante;

    @Column(length = 1000)
    private String observacao;

    public Long getId() { return id; }
    public Missao getMissao() { return missao; }
    public void setMissao(Missao missao) { this.missao = missao; }
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
    public LocalDateTime getInstante() { return instante; }
    public void setInstante(LocalDateTime instante) { this.instante = instante; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
