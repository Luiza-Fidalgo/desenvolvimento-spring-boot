package com.continuum.control.service;

import com.continuum.control.dto.IntervencaoRequest;
import com.continuum.control.dto.IntervencaoResponse;
import com.continuum.control.model.Agente;
import com.continuum.control.model.IntervencaoTemporal;
import com.continuum.control.model.Missao;
import com.continuum.control.model.enums.ImpactoIntervencao;
import com.continuum.control.model.enums.SituacaoMissao;
import com.continuum.control.repository.AnomaliaRepository;
import com.continuum.control.repository.IntervencaoTemporalRepository;
import com.continuum.control.repository.MissaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IntervencaoTemporalServiceTest {
    private IntervencaoTemporalRepository repository;
    private MissaoRepository missoes;
    private AnomaliaRepository anomalias;
    private IntervencaoTemporalService service;
    private Missao missao;
    private Agente agente;

    @BeforeEach
    void preparar() {
        repository = mock(IntervencaoTemporalRepository.class);
        missoes = mock(MissaoRepository.class);
        anomalias = mock(AnomaliaRepository.class);
        service = new IntervencaoTemporalService(repository, missoes, anomalias);
        missao = mock(Missao.class);
        agente = mock(Agente.class);
        when(missao.getId()).thenReturn(2L);
        when(agente.getId()).thenReturn(3L);
    }

    private IntervencaoRequest dados() {
        IntervencaoRequest dados = new IntervencaoRequest();
        dados.setAgenteId(3L);
        dados.setAcao("  Ajustar equipamento  ");
        dados.setJustificativa(" Corrigir os sinais ");
        dados.setImpactoEstimado(ImpactoIntervencao.CONTROLADO);
        dados.setResultadoObservado(" Sinal estabilizado ");
        return dados;
    }

    @Test
    void registrarIntervencaoEmMissaoEmExecucao() {
        when(missoes.findById(2L)).thenReturn(Optional.of(missao));
        when(missao.getSituacao()).thenReturn(SituacaoMissao.EM_EXECUCAO);
        when(missao.getAgentes()).thenReturn(Set.of(agente));
        when(repository.save(any(IntervencaoTemporal.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        IntervencaoResponse resposta = service.registrar(2L, dados());

        assertEquals(2L, resposta.missaoId());
        assertEquals(3L, resposta.agenteId());
        assertEquals("Ajustar equipamento", resposta.acao());
        assertEquals("Corrigir os sinais", resposta.justificativa());
        assertEquals("Sinal estabilizado", resposta.resultadoObservado());
        assertEquals(ImpactoIntervencao.CONTROLADO, resposta.impactoEstimado());
        assertNotNull(resposta.instante());
        verify(repository).save(any(IntervencaoTemporal.class));
    }

    @Test
    void impedirMissaoInexistente() {
        when(missoes.findById(99L)).thenReturn(Optional.empty());
        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.registrar(99L, dados()));
        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void impedirIntervencaoEmMissaoPlanejada() {
        when(missoes.findById(2L)).thenReturn(Optional.of(missao));
        when(missao.getSituacao()).thenReturn(SituacaoMissao.PLANEJADA);
        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.registrar(2L, dados()));
        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void impedirIntervencaoEmMissaoConcluida() {
        when(missoes.findById(2L)).thenReturn(Optional.of(missao));
        when(missao.getSituacao()).thenReturn(SituacaoMissao.CONCLUIDA);
        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.registrar(2L, dados()));
        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
    }

    @Test
    void impedirAgenteQueNaoParticipa() {
        when(missoes.findById(2L)).thenReturn(Optional.of(missao));
        when(missao.getSituacao()).thenReturn(SituacaoMissao.EM_EXECUCAO);
        when(missao.getAgentes()).thenReturn(Set.of());
        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.registrar(2L, dados()));
        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void listarPorMissao() {
        when(missoes.existsById(2L)).thenReturn(true);
        IntervencaoTemporal intervencao = new IntervencaoTemporal();
        intervencao.setMissao(missao);
        intervencao.setAgente(agente);
        intervencao.setImpactoEstimado(ImpactoIntervencao.MINIMO);
        when(repository.findByMissaoIdOrderByInstanteAsc(2L)).thenReturn(List.of(intervencao));
        assertEquals(1, service.listarPorMissao(2L).size());
    }

    @Test
    void listarPorAnomalia() {
        when(anomalias.existsById(1L)).thenReturn(true);
        when(repository.findByMissao_Anomalia_IdOrderByInstanteAsc(1L))
                .thenReturn(List.of());
        assertTrue(service.listarPorAnomalia(1L).isEmpty());
    }

    @Test
    void impedirConsultaPorMissaoInexistente() {
        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.listarPorMissao(88L));
        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }

    @Test
    void impedirConsultaPorAnomaliaInexistente() {
        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.listarPorAnomalia(88L));
        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }
}
