package com.continuum.control.service;

import com.continuum.control.dto.OcorrenciaRequest;
import com.continuum.control.dto.OcorrenciaResponse;
import com.continuum.control.model.Agente;
import com.continuum.control.model.Anomalia;
import com.continuum.control.model.Missao;
import com.continuum.control.model.OcorrenciaTemporal;
import com.continuum.control.model.enums.SituacaoMissao;
import com.continuum.control.model.enums.TipoOcorrencia;
import com.continuum.control.repository.AnomaliaRepository;
import com.continuum.control.repository.MissaoRepository;
import com.continuum.control.repository.OcorrenciaTemporalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OcorrenciaTemporalServiceTest {

    private OcorrenciaTemporalRepository repository;
    private MissaoRepository missaoRepository;
    private AnomaliaRepository anomaliaRepository;
    private OcorrenciaTemporalService service;

    @BeforeEach
    void preparar() {
        repository = mock(OcorrenciaTemporalRepository.class);
        missaoRepository = mock(MissaoRepository.class);
        anomaliaRepository = mock(AnomaliaRepository.class);
        service = new OcorrenciaTemporalService(repository, missaoRepository, anomaliaRepository);
    }

    private Missao missao(SituacaoMissao situacao) {
        Missao missao = new Missao();
        missao.setSituacao(situacao);
        Agente agente = mock(Agente.class);
        when(agente.getId()).thenReturn(2L);
        Anomalia anomalia = mock(Anomalia.class);
        when(anomalia.getId()).thenReturn(1L);
        missao.setAgentes(Set.of(agente));
        missao.setAnomalia(anomalia);
        return missao;
    }

    private OcorrenciaRequest dados() {
        OcorrenciaRequest dados = new OcorrenciaRequest();
        dados.setAgenteId(2L);
        dados.setTipo(TipoOcorrencia.DISTORCAO_TEMPORAL);
        dados.setDescricao("Oscilacao na linha temporal");
        dados.setDetalhes(Map.of("intensidade", 8, "duracaoMinutos", 12));
        return dados;
    }

    @Test
    void registrarComMissaoEmExecucao() {
        Missao missao = missao(SituacaoMissao.EM_EXECUCAO);
        when(missaoRepository.findById(3L)).thenReturn(Optional.of(missao));
        when(repository.save(any(OcorrenciaTemporal.class))).thenAnswer(c -> c.getArgument(0));

        OcorrenciaResponse resposta = service.registrar(3L, dados());

        assertEquals(3L, resposta.getMissaoId());
        assertEquals(1L, resposta.getAnomaliaId());
        assertEquals(8, resposta.getDetalhes().get("intensidade"));
        verify(repository).save(any(OcorrenciaTemporal.class));
    }

    @Test
    void impedirOcorrenciaEmMissaoPlanejada() {
        Missao missao = missao(SituacaoMissao.PLANEJADA);
        when(missaoRepository.findById(3L)).thenReturn(Optional.of(missao));

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.registrar(3L, dados()));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(repository, never()).save(any(OcorrenciaTemporal.class));
    }

    @Test
    void impedirAgenteForaDaMissao() {
        Missao missao = missao(SituacaoMissao.EM_EXECUCAO);
        when(missaoRepository.findById(3L)).thenReturn(Optional.of(missao));
        OcorrenciaRequest dados = dados();
        dados.setAgenteId(5L);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.registrar(3L, dados));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
    }

    @Test
    void impedirOcorrenciaEmMissaoInexistente() {
        when(missaoRepository.findById(90L)).thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.registrar(90L, dados()));

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }

    @Test
    void listarPorMissao() {
        OcorrenciaTemporal ocorrencia = new OcorrenciaTemporal();
        ocorrencia.setMissaoId(3L);
        when(missaoRepository.existsById(3L)).thenReturn(true);
        when(repository.findByMissaoIdOrderByInstanteAsc(3L)).thenReturn(List.of(ocorrencia));

        assertEquals(1, service.listarPorMissao(3L).size());
    }

    @Test
    void listarPorAnomalia() {
        OcorrenciaTemporal ocorrencia = new OcorrenciaTemporal();
        ocorrencia.setAnomaliaId(1L);
        when(anomaliaRepository.existsById(1L)).thenReturn(true);
        when(repository.findByAnomaliaIdOrderByInstanteAsc(1L)).thenReturn(List.of(ocorrencia));

        assertEquals(1, service.listarPorAnomalia(1L).size());
    }
}
