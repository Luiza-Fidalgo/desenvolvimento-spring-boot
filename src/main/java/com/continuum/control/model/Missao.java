package com.continuum.control.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import com.continuum.control.model.enums.TipoMissao;
import com.continuum.control.model.enums.PrioridadeMissao;
import com.continuum.control.model.enums.SituacaoAnomalia;
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

    // Informacoes de encerramento, preenchidas somente ao finalizar uma missao.
    @Column(length = 200)
    private String resultado;

    @Column(length = 2000)
    private String resumo;

    @Column(length = 1000)
    private String impactoObservado;

    @Column(length = 2000)
    private String observacoesEncerramento;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SituacaoAnomalia situacaoFinalAnomalia;

    private LocalDateTime encerradaEm;

    public Missao() { }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public Anomalia getAnomalia() { return anomalia; }
    public void setAnomalia(Anomalia anomalia) { this.anomalia = anomalia; }
    public LocalDate getDestinoTemporal() { return destinoTemporal; }
    public void setDestinoTemporal(LocalDate destinoTemporal) { this.destinoTemporal = destinoTemporal; }
    public TipoMissao getTipo() { return tipo; }
    public void setTipo(TipoMissao tipo) { this.tipo = tipo; }
    public PrioridadeMissao getPrioridade() { return prioridade; }
    public void setPrioridade(PrioridadeMissao prioridade) { this.prioridade = prioridade; }
    public SituacaoMissao getSituacao() { return situacao; }
    public void setSituacao(SituacaoMissao situacao) { this.situacao = situacao; }
    public Set<Agente> getAgentes() { return agentes; }
    public void setAgentes(Set<Agente> agentes) { this.agentes = agentes; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    public String getResumo() { return resumo; }
    public void setResumo(String resumo) { this.resumo = resumo; }
    public String getImpactoObservado() { return impactoObservado; }
    public void setImpactoObservado(String impactoObservado) { this.impactoObservado = impactoObservado; }
    public String getObservacoesEncerramento() { return observacoesEncerramento; }
    public void setObservacoesEncerramento(String observacoesEncerramento) { this.observacoesEncerramento = observacoesEncerramento; }
    public SituacaoAnomalia getSituacaoFinalAnomalia() { return situacaoFinalAnomalia; }
    public void setSituacaoFinalAnomalia(SituacaoAnomalia situacaoFinalAnomalia) { this.situacaoFinalAnomalia = situacaoFinalAnomalia; }
    public LocalDateTime getEncerradaEm() { return encerradaEm; }
    public void setEncerradaEm(LocalDateTime encerradaEm) { this.encerradaEm = encerradaEm; }
}
