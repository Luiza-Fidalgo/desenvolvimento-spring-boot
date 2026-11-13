package com.continuum.control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.continuum.control.model.Anomalia;
import java.util.List;
import com.continuum.control.model.enums.SituacaoAnomalia;
import com.continuum.control.model.enums.RiscoAnomalia;

public interface AnomaliaRepository extends JpaRepository<Anomalia, Long> {
    List<Anomalia> findBySituacao(SituacaoAnomalia situacao);
    List<Anomalia> findByRisco(RiscoAnomalia risco);
    List<Anomalia> findByEventoId(Long eventoId);
}
