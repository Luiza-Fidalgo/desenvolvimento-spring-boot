package com.continuum.control.service;

import com.continuum.control.dto.AgenteRequest;
import com.continuum.control.model.Agente;
import com.continuum.control.model.enums.SituacaoAgente;
import com.continuum.control.repository.AgenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AgenteServiceTest {

    private AgenteRepository repository;
    private AgenteService service;

    @BeforeEach
    void preparar() {
        repository = mock(AgenteRepository.class);
        service = new AgenteService(repository);
    }

    private AgenteRequest dados() {
        AgenteRequest dados = new AgenteRequest();
        dados.setCodigo("A001");
        dados.setNome("Ana");
        dados.setEspecialidade("Investigacao");
        return dados;
    }

    @Test
    void cadastrarAgenteDisponivel() {
        when(repository.save(any(Agente.class))).thenAnswer(chamada -> chamada.getArgument(0));

        Agente agente = service.cadastrar(dados());

        assertEquals("A001", agente.getCodigo());
        assertEquals("Ana", agente.getNome());
        assertEquals(SituacaoAgente.DISPONIVEL, agente.getSituacao());
        verify(repository).save(any(Agente.class));
    }

    @Test
    void impedirCodigoRepetido() {
        when(repository.existsByCodigo("A001")).thenReturn(true);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.cadastrar(dados()));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(repository, never()).save(any(Agente.class));
    }

    @Test
    void buscarAgenteInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.buscar(99L));

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }

    @Test
    void impedirInativacaoDeAgenteEmMissao() {
        Agente agente = new Agente();
        agente.setSituacao(SituacaoAgente.EM_MISSAO);
        when(repository.findById(1L)).thenReturn(Optional.of(agente));

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.inativar(1L));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(repository, never()).save(any(Agente.class));
    }

    @Test
    void inativarAgenteDisponivel() {
        Agente agente = new Agente();
        when(repository.findById(1L)).thenReturn(Optional.of(agente));
        when(repository.save(any(Agente.class))).thenAnswer(chamada -> chamada.getArgument(0));

        Agente resultado = service.inativar(1L);

        assertEquals(SituacaoAgente.INATIVO, resultado.getSituacao());
    }
}
