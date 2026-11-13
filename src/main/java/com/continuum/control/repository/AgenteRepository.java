package com.continuum.control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.continuum.control.model.Agente;
import java.util.List;
import com.continuum.control.model.enums.SituacaoAgente;

public interface AgenteRepository extends JpaRepository<Agente, Long> {
    List<Agente> findBySituacao(SituacaoAgente situacao);
    List<Agente> findByEspecialidadeIgnoreCase(String especialidade);
    boolean existsByCodigo(String codigo);
}
