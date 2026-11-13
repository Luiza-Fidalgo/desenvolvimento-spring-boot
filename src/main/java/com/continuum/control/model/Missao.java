package com.continuum.control.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import com.continuum.control.model.enums.TipoMissao;
import com.continuum.control.model.enums.PrioridadeMissao;
import com.continuum.control.model.enums.SituacaoMissao;

@Entity
@Table(name = "missoes")
public class Missao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 1000)
    private String objetivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anomalia_id", nullable = false)
    private Anomalia anomalia;

    @Column(nullable = false)
    private LocalDate destinoTemporal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMissao tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrioridadeMissao prioridade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SituacaoMissao situacao = SituacaoMissao.PLANEJADA;

    @ManyToMany
    @JoinTable(name = "missao_agentes",
        joinColumns = @JoinColumn(name = "missao_id"),
        inverseJoinColumns = @JoinColumn(name = "agente_id"))
    private Set<Agente> agentes = new HashSet<>();

    public Missao() {
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public Anomalia getAnomalia() {
        return anomalia;
    }

    public void setAnomalia(Anomalia anomalia) {
        this.anomalia = anomalia;
    }

    public LocalDate getDestinoTemporal() {
        return destinoTemporal;
    }

    public void setDestinoTemporal(LocalDate destinoTemporal) {
        this.destinoTemporal = destinoTemporal;
    }

    public TipoMissao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMissao tipo) {
        this.tipo = tipo;
    }

    public PrioridadeMissao getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(PrioridadeMissao prioridade) {
        this.prioridade = prioridade;
    }

    public SituacaoMissao getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoMissao situacao) {
        this.situacao = situacao;
    }

    public Set<Agente> getAgentes() {
        return agentes;
    }

    public void setAgentes(Set<Agente> agentes) {
        this.agentes = agentes;
    }
}
