package com.continuum.control.service;

import com.continuum.control.dto.EstadoOperacionalResponse;
import com.continuum.control.model.enums.RiscoAnomalia;
import com.continuum.control.model.enums.SituacaoAgente;
import com.continuum.control.model.enums.SituacaoAnomalia;
import com.continuum.control.model.enums.SituacaoMissao;
import com.continuum.control.repository.AgenteRepository;
import com.continuum.control.repository.AnomaliaRepository;
import com.continuum.control.repository.MissaoRepository;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class EstadoOperacionalService {

    private static final String CHAVE = "continuum:estado-operacional";
    private static final Duration TEMPO_CACHE = Duration.ofSeconds(15);

    private final AgenteRepository agenteRepository;
    private final AnomaliaRepository anomaliaRepository;
    private final MissaoRepository missaoRepository;
    private final StringRedisTemplate redis;

    public EstadoOperacionalService(AgenteRepository agenteRepository,
                                   AnomaliaRepository anomaliaRepository,
                                   MissaoRepository missaoRepository,
                                   StringRedisTemplate redis) {
        this.agenteRepository = agenteRepository;
        this.anomaliaRepository = anomaliaRepository;
        this.missaoRepository = missaoRepository;
        this.redis = redis;
    }

    public EstadoOperacionalResponse consultar(boolean atualizar) {
        if (!atualizar) {
            try {
                String salvo = redis.opsForValue().get(CHAVE);
                if (salvo != null) {
                    EstadoOperacionalResponse doCache = lerCache(salvo);
                    if (doCache != null) {
                        return doCache;
                    }
                }
            } catch (RedisConnectionFailureException erro) {
                // O Redis e um cache: mesmo indisponivel, o PostgreSQL continua funcionando.
                return calcular("POSTGRESQL_SEM_REDIS");
            }
        }

        EstadoOperacionalResponse novo = calcular("POSTGRESQL");
        try {
            redis.opsForValue().set(CHAVE, escreverCache(novo), TEMPO_CACHE);
        } catch (RedisConnectionFailureException erro) {
            return novo.comOrigem("POSTGRESQL_SEM_REDIS");
        }
        return novo;
    }

    private EstadoOperacionalResponse calcular(String origem) {
        long disponiveis = agenteRepository.countBySituacao(SituacaoAgente.DISPONIVEL);
        long ocupados = agenteRepository.countBySituacao(SituacaoAgente.EM_MISSAO);
        long emExecucao = missaoRepository.countBySituacao(SituacaoMissao.EM_EXECUCAO);
        long falhas = missaoRepository.countBySituacao(SituacaoMissao.FALHOU);

        // ESTABILIZADA e a unica situacao sem pendencia de estabilidade.
        long ativas = anomaliaRepository.countBySituacaoNot(SituacaoAnomalia.ESTABILIZADA);
        long criticas = anomaliaRepository.countByRiscoAndSituacaoNot(
                RiscoAnomalia.CRITICO, SituacaoAnomalia.ESTABILIZADA);

        // Formula simples e explicavel, definida para este projeto.
        // A penalidade de uma anomalia critica soma-se a penalidade de anomalia ativa.
        long pontosPerdidos = ativas * 8 + criticas * 12 + emExecucao * 3 + falhas * 4;
        int indice = (int) Math.max(0L, 100L - pontosPerdidos);

        String classificacao = "ESTAVEL";
        if (indice < 50) {
            classificacao = "CRITICO";
        } else if (indice < 80) {
            classificacao = "ATENCAO";
        }
        return new EstadoOperacionalResponse(disponiveis, ocupados, emExecucao,
                ativas, criticas, falhas, indice, classificacao, origem);
    }

    // Guardamos apenas numeros e uma palavra. O Redis e chave-valor, nao banco relacional.
    private String escreverCache(EstadoOperacionalResponse estado) {
        return estado.agentesDisponiveis() + ";" + estado.agentesEmMissao() + ";" +
                estado.missoesEmExecucao() + ";" + estado.anomaliasAtivas() + ";" +
                estado.anomaliasCriticas() + ";" + estado.missoesFalharam() + ";" +
                estado.indiceEstabilidade() + ";" + estado.classificacao();
    }

    private EstadoOperacionalResponse lerCache(String salvo) {
        String[] valores = salvo.split(";");
        if (valores.length != 8) {
            return null;
        }
        try {
            return new EstadoOperacionalResponse(
                    Long.parseLong(valores[0]), Long.parseLong(valores[1]),
                    Long.parseLong(valores[2]), Long.parseLong(valores[3]),
                    Long.parseLong(valores[4]), Long.parseLong(valores[5]),
                    Integer.parseInt(valores[6]), valores[7], "REDIS");
        } catch (NumberFormatException erro) {
            return null;
        }
    }
}
