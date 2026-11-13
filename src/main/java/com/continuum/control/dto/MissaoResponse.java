package com.continuum.control.dto;

import com.continuum.control.model.Agente;
import com.continuum.control.model.Missao;
import com.continuum.control.model.enums.PrioridadeMissao;
import com.continuum.control.model.enums.SituacaoMissao;
import com.continuum.control.model.enums.SituacaoAnomalia;
import com.continuum.control.model.enums.TipoMissao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MissaoResponse {

    private final Long id;
    private final String codigo;
    private final String objetivo;
    private final Long anomaliaId;
    private final LocalDate destinoTemporal;
    private final TipoMissao tipo;
    private final PrioridadeMissao prioridade;
    private final SituacaoMissao situacao;
    private final List<Long> agentesIds;
    private final String resultado;
    private final String resumo;
    private final String impactoObservado;
    private final String observacoesEncerramento;
    private final SituacaoAnomalia situacaoFinalAnomalia;
    private final LocalDateTime encerradaEm;

    public MissaoResponse(Missao missao) {
        this.id = missao.getId();
        this.codigo = missao.getCodigo();
        this.objetivo = missao.getObjetivo();
        this.anomaliaId = missao.getAnomalia().getId();
        this.destinoTemporal = missao.getDestinoTemporal();
        this.tipo = missao.getTipo();
        this.prioridade = missao.getPrioridade();
        this.situacao = missao.getSituacao();
        this.agentesIds = missao.getAgentes().stream().map(Agente::getId).toList();
        this.resultado = missao.getResultado();
        this.resumo = missao.getResumo();
        this.impactoObservado = missao.getImpactoObservado();
        this.observacoesEncerramento = missao.getObservacoesEncerramento();
        this.situacaoFinalAnomalia = missao.getSituacaoFinalAnomalia();
        this.encerradaEm = missao.getEncerradaEm();
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public Long getAnomaliaId() {
        return anomaliaId;
    }

    public LocalDate getDestinoTemporal() {
        return destinoTemporal;
    }

    public TipoMissao getTipo() {
        return tipo;
    }

    public PrioridadeMissao getPrioridade() {
        return prioridade;
    }

    public SituacaoMissao getSituacao() {
        return situacao;
    }

    public List<Long> getAgentesIds() {
        return agentesIds;
    }

    public String getResultado() { return resultado; }
    public String getResumo() { return resumo; }
    public String getImpactoObservado() { return impactoObservado; }
    public String getObservacoesEncerramento() { return observacoesEncerramento; }
    public SituacaoAnomalia getSituacaoFinalAnomalia() { return situacaoFinalAnomalia; }
    public LocalDateTime getEncerradaEm() { return encerradaEm; }
}
