package com.continuum.control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.continuum.control.model.EventoHistorico;
import java.util.List;
import java.time.LocalDate;
import com.continuum.control.model.enums.ImportanciaEvento;

public interface EventoHistoricoRepository extends JpaRepository<EventoHistorico, Long> {
    List<EventoHistorico> findByImportancia(ImportanciaEvento importancia);
    List<EventoHistorico> findByDataBetween(LocalDate inicio, LocalDate fim);
}
