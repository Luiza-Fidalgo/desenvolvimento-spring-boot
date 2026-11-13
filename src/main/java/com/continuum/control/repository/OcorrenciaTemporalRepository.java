package com.continuum.control.repository;

import com.continuum.control.model.OcorrenciaTemporal;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OcorrenciaTemporalRepository extends MongoRepository<OcorrenciaTemporal, String> {
    List<OcorrenciaTemporal> findByMissaoIdOrderByInstanteAsc(Long missaoId);
    List<OcorrenciaTemporal> findByAnomaliaIdOrderByInstanteAsc(Long anomaliaId);
}
