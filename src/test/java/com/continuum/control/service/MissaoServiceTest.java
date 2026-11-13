package com.continuum.control.service;

import com.continuum.control.dto.MissaoRequest;
import com.continuum.control.dto.MissaoResponse;
import com.continuum.control.dto.AutorizacaoRequest;
import com.continuum.control.dto.EncerramentoMissaoRequest;
import com.continuum.control.model.Agente;
import com.continuum.control.model.Autorizacao;
import com.continuum.control.model.Anomalia;
import com.continuum.control.model.Missao;
import com.continuum.control.model.enums.PrioridadeMissao;
import com.continuum.control.model.enums.SituacaoAgente;
import com.continuum.control.model.enums.SituacaoAnomalia;
import com.continuum.control.model.enums.SituacaoMissao;
import com.continuum.control.model.enums.TipoMissao;
import com.continuum.control.repository.AgenteRepository;
import com.continuum.control.repository.AutorizacaoRepository;
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
    private AutorizacaoRepository autorizacaoRepository;

    @BeforeEach
    void preparar() {
        repository = mock(MissaoRepository.class);
        anomaliaRepository = mock(AnomaliaRepository.class);
        agenteRepository = mock(AgenteRepository.class);
        autorizacaoRepository = mock(AutorizacaoRepository.class);
        service = new MissaoService(repository, anomaliaRepository, agenteRepository, autorizacaoRepository);
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


    private AutorizacaoRequest dadosAutorizacao() {
        AutorizacaoRequest dados = new AutorizacaoRequest();
        dados.setResponsavel("Supervisor");
        dados.setObservacao("Missao aprovada");
        return dados;
    }

    private EncerramentoMissaoRequest dadosEncerramento() {
        EncerramentoMissaoRequest dados = new EncerramentoMissaoRequest();
        dados.setResultado("Objetivo atingido");
        dados.setResumo("Operacao realizada");
        dados.setImpactoObservado("Baixo");
        return dados;
    }

    private Agente agenteDisponivel() {
        Agente agente = new Agente();
        agente.setSituacao(SituacaoAgente.DISPONIVEL);
        return agente;
    }

    @Test
    void autorizarMissaoPlanejada() {
        Missao existente = missao(SituacaoMissao.PLANEJADA);
        existente.getAgentes().add(agenteDisponivel());
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Missao.class))).thenAnswer(chamada -> chamada.getArgument(0));

        assertEquals(SituacaoMissao.AUTORIZADA,
                service.autorizar(1L, dadosAutorizacao()).getSituacao());
        verify(autorizacaoRepository).save(any(Autorizacao.class));
    }

    @Test
    void impedirAutorizarSemAgentes() {
        Missao existente = missao(SituacaoMissao.PLANEJADA);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.autorizar(1L, dadosAutorizacao()));
        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(autorizacaoRepository, never()).save(any(Autorizacao.class));
    }

    @Test
    void iniciarMissaoEAtualizarAgente() {
        Missao existente = missao(SituacaoMissao.AUTORIZADA);
        Agente agente = agenteDisponivel();
        existente.getAgentes().add(agente);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Missao.class))).thenAnswer(chamada -> chamada.getArgument(0));

        assertEquals(SituacaoMissao.EM_EXECUCAO, service.iniciar(1L).getSituacao());
        assertEquals(SituacaoAgente.EM_MISSAO, agente.getSituacao());
        verify(agenteRepository).saveAll(any());
    }

    @Test
    void iniciarCorrecaoEAlterarAnomalia() {
        Missao existente = missao(SituacaoMissao.AUTORIZADA);
        existente.setTipo(TipoMissao.CORRECAO);
        existente.getAgentes().add(agenteDisponivel());
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Missao.class))).thenAnswer(chamada -> chamada.getArgument(0));

        service.iniciar(1L);

        assertEquals(SituacaoAnomalia.EM_CORRECAO, existente.getAnomalia().getSituacao());
        verify(anomaliaRepository).save(any(Anomalia.class));
    }

    @Test
    void impedirIniciarSemAutorizacao() {
        Missao existente = missao(SituacaoMissao.PLANEJADA);
        existente.getAgentes().add(agenteDisponivel());
        when(repository.findById(1L)).thenReturn(Optional.of(existente));

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.iniciar(1L));
        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
    }

    @Test
    void concluirCorrecaoEEstabilizarAnomalia() {
        Missao existente = missao(SituacaoMissao.EM_EXECUCAO);
        existente.setTipo(TipoMissao.CORRECAO);
        existente.getAnomalia().setSituacao(SituacaoAnomalia.EM_CORRECAO);
        Agente agente = agenteDisponivel();
        agente.setSituacao(SituacaoAgente.EM_MISSAO);
        existente.getAgentes().add(agente);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Missao.class))).thenAnswer(chamada -> chamada.getArgument(0));
        EncerramentoMissaoRequest dados = dadosEncerramento();
        dados.setSituacaoFinalAnomalia(SituacaoAnomalia.ESTABILIZADA);

        assertEquals(SituacaoMissao.CONCLUIDA, service.concluir(1L, dados).getSituacao());
        assertEquals(SituacaoAnomalia.ESTABILIZADA, existente.getAnomalia().getSituacao());
        assertEquals(SituacaoAgente.DISPONIVEL, agente.getSituacao());
    }

    @Test
    void falharCorrecaoSemResolverAnomalia() {
        Missao existente = missao(SituacaoMissao.EM_EXECUCAO);
        existente.setTipo(TipoMissao.CORRECAO);
        existente.getAnomalia().setSituacao(SituacaoAnomalia.EM_CORRECAO);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Missao.class))).thenAnswer(chamada -> chamada.getArgument(0));

        assertEquals(SituacaoMissao.FALHOU, service.falhar(1L, dadosEncerramento()).getSituacao());
        assertEquals(SituacaoAnomalia.CONFIRMADA, existente.getAnomalia().getSituacao());
    }
}
