package com.continuum.control.service;

import com.continuum.control.dto.AnomaliaRequest;
import com.continuum.control.dto.AnomaliaResponse;
import com.continuum.control.model.Anomalia;
import com.continuum.control.model.EventoHistorico;
import com.continuum.control.model.enums.RiscoAnomalia;
import com.continuum.control.model.enums.SituacaoAnomalia;
import com.continuum.control.repository.AnomaliaRepository;
import com.continuum.control.repository.EventoHistoricoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AnomaliaServiceTest {

    private AnomaliaRepository repository;
    private EventoHistoricoRepository eventoRepository;
    private AnomaliaService service;

    @BeforeEach
    void preparar() {
        repository = mock(AnomaliaRepository.class);
        eventoRepository = mock(EventoHistoricoRepository.class);
        service = new AnomaliaService(repository, eventoRepository);
    }

    private AnomaliaRequest dados() {
        AnomaliaRequest dados = new AnomaliaRequest();
        dados.setEventoId(1L);
        dados.setDescricao("Divergencia detectada");
        dados.setDataDeteccao(LocalDateTime.of(2026, 11, 12, 20, 0));
        dados.setRisco(RiscoAnomalia.CRITICO);
        dados.setOrigem("Desconhecida");
        dados.setObservacoes("Em investigacao");
        return dados;
    }

    private EventoHistorico evento() {
        EventoHistorico evento = mock(EventoHistorico.class);
        when(evento.getId()).thenReturn(1L);
        return evento;
    }

    private Anomalia anomalia(SituacaoAnomalia situacao) {
        Anomalia anomalia = new Anomalia();
        anomalia.setEvento(evento());
        anomalia.setSituacao(situacao);
        return anomalia;
    }

    @Test
    void cadastrarComEventoExistente() {
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento()));
        when(repository.save(any(Anomalia.class))).thenAnswer(chamada -> chamada.getArgument(0));

        AnomaliaResponse resposta = service.cadastrar(dados());

        assertEquals(1L, resposta.getEventoId());
        assertEquals(RiscoAnomalia.CRITICO, resposta.getRisco());
        assertEquals(SituacaoAnomalia.DETECTADA, resposta.getSituacao());
        verify(repository).save(any(Anomalia.class));
    }

    @Test
    void impedirCadastroSemEventoExistente() {
        when(eventoRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.cadastrar(dados()));

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
        verify(repository, never()).save(any(Anomalia.class));
    }

    @Test
    void buscarAnomaliaInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.buscar(99L));

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }

    @Test
    void listarAnomalias() {
        when(repository.findAll()).thenReturn(List.of(anomalia(SituacaoAnomalia.DETECTADA)));
        assertEquals(1, service.listar().size());
    }

    @Test
    void filtrarPorRisco() {
        when(repository.findByRisco(RiscoAnomalia.CRITICO))
                .thenReturn(List.of(anomalia(SituacaoAnomalia.DETECTADA)));
        assertEquals(1, service.listarPorRisco(RiscoAnomalia.CRITICO).size());
    }

    @Test
    void filtrarPorSituacao() {
        when(repository.findBySituacao(SituacaoAnomalia.CONFIRMADA))
                .thenReturn(List.of(anomalia(SituacaoAnomalia.CONFIRMADA)));
        assertEquals(1, service.listarPorSituacao(SituacaoAnomalia.CONFIRMADA).size());
    }

    @Test
    void filtrarPorEvento() {
        when(repository.findByEventoId(1L))
                .thenReturn(List.of(anomalia(SituacaoAnomalia.DETECTADA)));
        assertEquals(1, service.listarPorEvento(1L).size());
    }

    @Test
    void atualizarAnomaliaEmAnalise() {
        Anomalia existente = anomalia(SituacaoAnomalia.EM_ANALISE);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento()));
        when(repository.save(any(Anomalia.class))).thenAnswer(chamada -> chamada.getArgument(0));

        AnomaliaResponse resposta = service.atualizar(1L, dados());

        assertEquals("Divergencia detectada", resposta.getDescricao());
        assertEquals(SituacaoAnomalia.EM_ANALISE, resposta.getSituacao());
    }

    @Test
    void impedirAtualizarIrreversivel() {
        when(repository.findById(1L)).thenReturn(Optional.of(anomalia(SituacaoAnomalia.IRREVERSIVEL)));

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.atualizar(1L, dados()));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(repository, never()).save(any(Anomalia.class));
    }

    @Test
    void iniciarAnalise() {
        when(repository.findById(1L)).thenReturn(Optional.of(anomalia(SituacaoAnomalia.DETECTADA)));
        when(repository.save(any(Anomalia.class))).thenAnswer(chamada -> chamada.getArgument(0));

        assertEquals(SituacaoAnomalia.EM_ANALISE, service.iniciarAnalise(1L).getSituacao());
    }

    @Test
    void impedirAnaliseDuplicada() {
        when(repository.findById(1L)).thenReturn(Optional.of(anomalia(SituacaoAnomalia.EM_ANALISE)));

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.iniciarAnalise(1L));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
    }

    @Test
    void confirmarAposAnalise() {
        when(repository.findById(1L)).thenReturn(Optional.of(anomalia(SituacaoAnomalia.EM_ANALISE)));
        when(repository.save(any(Anomalia.class))).thenAnswer(chamada -> chamada.getArgument(0));

        assertEquals(SituacaoAnomalia.CONFIRMADA, service.confirmar(1L).getSituacao());
    }

    @Test
    void impedirConfirmarSemAnalise() {
        when(repository.findById(1L)).thenReturn(Optional.of(anomalia(SituacaoAnomalia.DETECTADA)));

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.confirmar(1L));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
    }

    @Test
    void marcarIrreversivelAposConfirmacao() {
        when(repository.findById(1L)).thenReturn(Optional.of(anomalia(SituacaoAnomalia.CONFIRMADA)));
        when(repository.save(any(Anomalia.class))).thenAnswer(chamada -> chamada.getArgument(0));

        assertEquals(SituacaoAnomalia.IRREVERSIVEL, service.marcarIrreversivel(1L).getSituacao());
    }

    @Test
    void impedirIrreversivelSemAnalise() {
        when(repository.findById(1L)).thenReturn(Optional.of(anomalia(SituacaoAnomalia.DETECTADA)));

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.marcarIrreversivel(1L));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
    }
}
