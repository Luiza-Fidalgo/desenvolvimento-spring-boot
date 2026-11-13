package com.continuum.control.service;

import com.continuum.control.model.Agente;
import com.continuum.control.repository.AgenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PermissaoAgenteServiceTest {

    private AgenteRepository repository;
    private PermissaoAgenteService service;

    @BeforeEach
    void preparar() {
        repository = mock(AgenteRepository.class);
        service = new PermissaoAgenteService(repository);
    }

    private Authentication usuario(String nome, String perfil) {
        return new UsernamePasswordAuthenticationToken(nome, "senha",
                List.of(new SimpleGrantedAuthority("ROLE_" + perfil)));
    }

    @Test
    void agentePodeUsarProprioCodigo() {
        Agente agente = new Agente();
        agente.setCodigo("AG-01");
        when(repository.findById(1L)).thenReturn(Optional.of(agente));

        assertDoesNotThrow(() -> service.verificarRegistro(1L, usuario("AG-01", "AGENTE")));
    }

    @Test
    void agenteNaoPodeUsarCodigoDeOutro() {
        Agente agente = new Agente();
        agente.setCodigo("AG-02");
        when(repository.findById(2L)).thenReturn(Optional.of(agente));

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.verificarRegistro(2L, usuario("AG-01", "AGENTE")));
        assertEquals(HttpStatus.FORBIDDEN, erro.getStatusCode());
    }

    @Test
    void agenteInexistenteRetorna404() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.verificarRegistro(99L, usuario("AG-01", "AGENTE")));
        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }

    @Test
    void supervisorPodeRegistrarEmNomeDeUmAgente() {
        assertDoesNotThrow(() -> service.verificarRegistro(2L, usuario("supervisor", "SUPERVISOR")));
        verifyNoInteractions(repository);
    }
}
