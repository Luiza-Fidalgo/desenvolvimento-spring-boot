package com.continuum.control.service;

import com.continuum.control.dto.EstadoOperacionalResponse;
import com.continuum.control.model.enums.RiscoAnomalia;
import com.continuum.control.model.enums.SituacaoAgente;
import com.continuum.control.model.enums.SituacaoAnomalia;
import com.continuum.control.model.enums.SituacaoMissao;
import com.continuum.control.repository.AgenteRepository;
import com.continuum.control.repository.AnomaliaRepository;
import com.continuum.control.repository.MissaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EstadoOperacionalServiceTest {

    private AgenteRepository agentes;
    private AnomaliaRepository anomalias;
    private MissaoRepository missoes;
    private StringRedisTemplate redis;
    private ValueOperations<String, String> valores;
    private EstadoOperacionalService service;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void preparar() {
        agentes = mock(AgenteRepository.class);
        anomalias = mock(AnomaliaRepository.class);
        missoes = mock(MissaoRepository.class);
        redis = mock(StringRedisTemplate.class);
        valores = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(valores);
        service = new EstadoOperacionalService(agentes, anomalias, missoes, redis);
    }

    @Test
    void semAnomaliasIndiceEhCem() {
        EstadoOperacionalResponse resposta = service.consultar(false);

        assertEquals(100, resposta.indiceEstabilidade());
        assertEquals("ESTAVEL", resposta.classificacao());
        assertEquals("POSTGRESQL", resposta.origem());
    }

    @Test
    void descontaPontosPelasOcorrenciasOperacionais() {
        when(agentes.countBySituacao(SituacaoAgente.DISPONIVEL)).thenReturn(3L);
        when(agentes.countBySituacao(SituacaoAgente.EM_MISSAO)).thenReturn(1L);
        when(anomalias.countBySituacaoNot(SituacaoAnomalia.ESTABILIZADA)).thenReturn(1L);
        when(anomalias.countByRiscoAndSituacaoNot(RiscoAnomalia.CRITICO,
                SituacaoAnomalia.ESTABILIZADA)).thenReturn(1L);
        when(missoes.countBySituacao(SituacaoMissao.EM_EXECUCAO)).thenReturn(1L);
        when(missoes.countBySituacao(SituacaoMissao.FALHOU)).thenReturn(1L);

        EstadoOperacionalResponse resposta = service.consultar(false);

        assertEquals(73, resposta.indiceEstabilidade());
        assertEquals("ATENCAO", resposta.classificacao());
        assertEquals(3, resposta.agentesDisponiveis());
        assertEquals(1, resposta.agentesEmMissao());
        assertEquals(1, resposta.anomaliasCriticas());
    }

    @Test
    void utilizaRedisQuandoExisteCache() {
        when(valores.get("continuum:estado-operacional"))
                .thenReturn("3;1;2;4;1;1;41;CRITICO");

        EstadoOperacionalResponse resposta = service.consultar(false);

        assertEquals(41, resposta.indiceEstabilidade());
        assertEquals("REDIS", resposta.origem());
        verifyNoInteractions(agentes, anomalias, missoes);
    }

    @Test
    void forcarAtualizacaoConsultaBancoNovamente() {
        when(valores.get("continuum:estado-operacional"))
                .thenReturn("3;1;2;4;1;1;41;CRITICO");

        EstadoOperacionalResponse resposta = service.consultar(true);

        assertEquals("POSTGRESQL", resposta.origem());
        verify(valores, never()).get(anyString());
        verify(valores).set(eq("continuum:estado-operacional"), anyString(),
                eq(Duration.ofSeconds(15)));
        verify(anomalias).countBySituacaoNot(SituacaoAnomalia.ESTABILIZADA);
    }

    @Test
    void indiceNaoFicaAbaixoDeZero() {
        when(anomalias.countBySituacaoNot(SituacaoAnomalia.ESTABILIZADA)).thenReturn(20L);

        EstadoOperacionalResponse resposta = service.consultar(false);

        assertEquals(0, resposta.indiceEstabilidade());
        assertEquals("CRITICO", resposta.classificacao());
    }

    @Test
    void redisIndisponivelNaoBloqueiaConsulta() {
        when(valores.get("continuum:estado-operacional"))
                .thenThrow(new RedisConnectionFailureException("Redis indisponivel"));

        EstadoOperacionalResponse resposta = service.consultar(false);

        assertEquals(100, resposta.indiceEstabilidade());
        assertEquals("POSTGRESQL_SEM_REDIS", resposta.origem());
    }
}
