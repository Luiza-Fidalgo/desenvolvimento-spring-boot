package com.continuum.control.dto;

import com.continuum.control.model.IntervencaoTemporal;
import com.continuum.control.model.enums.ImpactoIntervencao;
import java.time.LocalDateTime;

public record IntervencaoResponse(
        Long id, Long missaoId, Long agenteId, LocalDateTime instante,
        String acao, String justificativa, ImpactoIntervencao impactoEstimado,
        String resultadoObservado) {

    public IntervencaoResponse(IntervencaoTemporal intervencao) {
        this(intervencao.getId(), intervencao.getMissao().getId(),
                intervencao.getAgente().getId(), intervencao.getInstante(),
                intervencao.getAcao(), intervencao.getJustificativa(),
                intervencao.getImpactoEstimado(), intervencao.getResultadoObservado());
    }
}
