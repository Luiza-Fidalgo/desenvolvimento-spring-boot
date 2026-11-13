package com.continuum.control.dto;

// Resumo que o painel operacional mostra para o usuario.
public record EstadoOperacionalResponse(
        long agentesDisponiveis,
        long agentesEmMissao,
        long missoesEmExecucao,
        long anomaliasAtivas,
        long anomaliasCriticas,
        long missoesFalharam,
        int indiceEstabilidade,
        String classificacao,
        String origem
) {
    public EstadoOperacionalResponse comOrigem(String novaOrigem) {
        return new EstadoOperacionalResponse(agentesDisponiveis, agentesEmMissao,
                missoesEmExecucao, anomaliasAtivas, anomaliasCriticas,
                missoesFalharam, indiceEstabilidade, classificacao, novaOrigem);
    }
}
