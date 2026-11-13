package com.continuum.control.service;

import com.continuum.control.dto.MissaoRequest;
import com.continuum.control.dto.MissaoResponse;
import com.continuum.control.model.Agente;
import com.continuum.control.model.Anomalia;
import com.continuum.control.model.Missao;
import com.continuum.control.model.enums.PrioridadeMissao;
import com.continuum.control.model.enums.SituacaoAgente;
import com.continuum.control.model.enums.SituacaoAnomalia;
import com.continuum.control.model.enums.SituacaoMissao;
import com.continuum.control.model.enums.TipoMissao;
import com.continuum.control.repository.AgenteRepository;
import com.continuum.control.repository.AnomaliaRepository;
import com.continuum.control.repository.MissaoRepository;
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

class MissaoServiceTest {

    private MissaoRepository repository;
    private AnomaliaRepository anomaliaRepository;
    private AgenteRepository agenteRepository;
    private MissaoService service;

    @BeforeEach
    void preparar() {
        repository = mock(MissaoRepository.class);
        anomaliaRepository = mock(AnomaliaRepository.class);
        agenteRepository = mock(AgenteRepository.class);
        service = new MissaoService(repository, anomaliaRepository, agenteRepository);
    }

    private MissaoRequest dados() {
        MissaoRequest dados = new MissaoRequest();
        dados.setCodigo("M001");
        dados.setObjetivo("Investigar divergencia");
        dados.setAnomaliaId(1L);
        dados.setDestinoTemporal(LocalDate.of(1989, 11, 9));
        dados.setTipo(TipoMissao.INVESTIGACAO);
        dados.setPrioridade(PrioridadeMissao.NORMAL);
        return dados;
    }

    private Anomalia anomalia(SituacaoAnomalia situacao) {
        Anomalia anomalia = new Anomalia();
        anomalia.setSituacao(situacao);
        return anomalia;
    }

    private Missao missao(SituacaoMissao situacao) {
        Missao missao = new Missao();
        missao.setCodigo("M001");
        missao.setAnomalia(anomalia(SituacaoAnomalia.CONFIRMADA));
        missao.setSituacao(situacao);
        return missao;
    }

    @Test
    void cadastrarMissaoPlanejada() {
        Anomalia anomalia = anomalia(SituacaoAnomalia.EM_ANALISE);
        when(anomaliaRepository.findById(1L)).thenReturn(Optional.of(anomalia));
        when(repository.save(any(Missao.class))).thenAnswer(chamada -> chamada.getArgument(0));

        MissaoResponse resultado = service.cadastrar(dados());

        assertEquals("M001", resultado.getCodigo());
        assertEquals(SituacaoMissao.PLANEJADA, resultado.getSituacao());
        verify(repository).save(any(Missao.class));
    }

    @Test
    void impedirCodigoDuplicado() {
        when(repository.existsByCodigo("M001")).thenReturn(true);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.cadastrar(dados()));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(repository, never()).save(any(Missao.class));
    }

    @Test
    void impedirMissaoSemAnomalia() {
        when(anomaliaRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.cadastrar(dados()));

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }

    @Test
    void impedirCorrecaoDeAnomaliaIrreversivel() {
        MissaoRequest dados = dados();
        dados.setTipo(TipoMissao.CORRECAO);
        Anomalia anomalia = anomalia(SituacaoAnomalia.IRREVERSIVEL);
        when(anomaliaRepository.findById(1L)).thenReturn(Optional.of(anomalia));

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.cadastrar(dados));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
    }

    @Test
    void impedirAgenteIndisponivel() {
        MissaoRequest dados = dados();
        dados.setAgentesIds(List.of(5L));
        Anomalia anomalia = anomalia(SituacaoAnomalia.CONFIRMADA);
        Agente agente = new Agente();
        agente.setSituacao(SituacaoAgente.EM_MISSAO);
        when(anomaliaRepository.findById(1L)).thenReturn(Optional.of(anomalia));
        when(agenteRepository.findById(5L)).thenReturn(Optional.of(agente));

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.cadastrar(dados));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
    }

    @Test
    void cadastrarComAgenteDisponivel() {
        MissaoRequest dados = dados();
        dados.setAgentesIds(List.of(5L));
        Anomalia anomalia = anomalia(SituacaoAnomalia.CONFIRMADA);
        Agente agente = new Agente();
        agente.setSituacao(SituacaoAgente.DISPONIVEL);

        when(anomaliaRepository.findById(1L)).thenReturn(Optional.of(anomalia));
        when(agenteRepository.findById(5L)).thenReturn(Optional.of(agente));
        when(repository.save(any(Missao.class))).thenAnswer(chamada -> chamada.getArgument(0));

        MissaoResponse resposta = service.cadastrar(dados);

        assertEquals(1, resposta.getAgentesIds().size());
        verify(agenteRepository).findById(5L);
    }

    @Test
    void impedirAlterarMissaoEmExecucao() {
        Missao existente = missao(SituacaoMissao.EM_EXECUCAO);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.atualizar(1L, dados()));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(repository, never()).save(any(Missao.class));
    }

    @Test
    void cancelarMissaoPlanejada() {
        Missao existente = missao(SituacaoMissao.PLANEJADA);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Missao.class))).thenAnswer(chamada -> chamada.getArgument(0));

        assertEquals(SituacaoMissao.CANCELADA, service.cancelar(1L).getSituacao());
    }
}
