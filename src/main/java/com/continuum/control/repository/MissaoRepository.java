package com.continuum.control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.continuum.control.model.Missao;
import java.util.List;
import com.continuum.control.model.enums.SituacaoMissao;

public interface MissaoRepository extends JpaRepository<Missao, Long> {
    List<Missao> findBySituacao(SituacaoMissao situacao);
    List<Missao> findByAnomaliaId(Long anomaliaId);
    List<Missao> findDistinctByAgentesId(Long agenteId);
    boolean existsByCodigo(String codigo);
    long countBySituacao(SituacaoMissao situacao);
}
