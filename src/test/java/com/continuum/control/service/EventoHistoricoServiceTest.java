package com.continuum.control.service;

import com.continuum.control.dto.EventoHistoricoRequest;
import com.continuum.control.model.EventoHistorico;
import com.continuum.control.model.enums.ImportanciaEvento;
import com.continuum.control.repository.EventoHistoricoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventoHistoricoServiceTest {

    private EventoHistoricoRepository repository;
    private EventoHistoricoService service;

    @BeforeEach
    void preparar() {
        repository = mock(EventoHistoricoRepository.class);
        service = new EventoHistoricoService(repository);
    }

    private EventoHistoricoRequest dados() {
        EventoHistoricoRequest dados = new EventoHistoricoRequest();
        dados.setNome("Primeira viagem temporal");
        dados.setDescricao("Evento monitorado");
        dados.setData(LocalDate.of(1990, 5, 10));
        dados.setLocalizacao("Rio de Janeiro");
        dados.setImportancia(ImportanciaEvento.ALTA);
        return dados;
    }

    @Test
    void cadastrarEvento() {
        when(repository.save(any(EventoHistorico.class))).thenAnswer(chamada -> chamada.getArgument(0));

        EventoHistorico evento = service.cadastrar(dados());

        assertEquals("Primeira viagem temporal", evento.getNome());
        assertEquals(ImportanciaEvento.ALTA, evento.getImportancia());
        verify(repository).save(any(EventoHistorico.class));
    }

    @Test
    void listarEventos() {
        when(repository.findAll()).thenReturn(List.of(new EventoHistorico()));

        assertEquals(1, service.listar().size());
    }

    @Test
    void buscarEventoInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.buscar(99L));

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }

    @Test
    void atualizarEvento() {
        EventoHistorico evento = new EventoHistorico();
        when(repository.findById(1L)).thenReturn(Optional.of(evento));
        when(repository.save(any(EventoHistorico.class))).thenAnswer(chamada -> chamada.getArgument(0));

        EventoHistorico atualizado = service.atualizar(1L, dados());

        assertEquals("Rio de Janeiro", atualizado.getLocalizacao());
        assertEquals(LocalDate.of(1990, 5, 10), atualizado.getData());
    }

    @Test
    void filtrarPorImportancia() {
        when(repository.findByImportancia(ImportanciaEvento.ALTA))
                .thenReturn(List.of(new EventoHistorico()));

        assertEquals(1, service.listarPorImportancia(ImportanciaEvento.ALTA).size());
    }

    @Test
    void filtrarPorPeriodo() {
        LocalDate inicio = LocalDate.of(1980, 1, 1);
        LocalDate fim = LocalDate.of(2000, 12, 31);
        when(repository.findByDataBetween(inicio, fim)).thenReturn(List.of(new EventoHistorico()));

        assertEquals(1, service.listarPorPeriodo(inicio, fim).size());
    }

    @Test
    void impedirPeriodoInvertido() {
        LocalDate inicio = LocalDate.of(2020, 1, 1);
        LocalDate fim = LocalDate.of(2010, 1, 1);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.listarPorPeriodo(inicio, fim));

        assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());
        verify(repository, never()).findByDataBetween(any(), any());
    }
}
