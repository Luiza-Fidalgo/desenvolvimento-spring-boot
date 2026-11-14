package com.continuum.control.repository;

import com.continuum.control.model.IntervencaoTemporal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IntervencaoTemporalRepository extends JpaRepository<IntervencaoTemporal, Long> {
    List<IntervencaoTemporal> findByMissaoIdOrderByInstanteAsc(Long missaoId);
    List<IntervencaoTemporal> findByMissao_Anomalia_IdOrderByInstanteAsc(Long anomaliaId);
}
