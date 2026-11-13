package com.continuum.control.repository;

import com.continuum.control.model.Autorizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AutorizacaoRepository extends JpaRepository<Autorizacao, Long> {
    List<Autorizacao> findByMissaoId(Long missaoId);
}
