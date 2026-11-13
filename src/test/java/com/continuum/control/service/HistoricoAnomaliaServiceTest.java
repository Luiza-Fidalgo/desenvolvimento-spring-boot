package com.continuum.control.service;

import com.continuum.control.dto.HistoricoItemResponse;
import com.continuum.control.model.Anomalia;
import com.continuum.control.model.Autorizacao;
import com.continuum.control.model.Missao;
import com.continuum.control.model.OcorrenciaTemporal;
import com.continuum.control.model.enums.RiscoAnomalia;
import com.continuum.control.model.enums.SituacaoMissao;
import com.continuum.control.model.enums.TipoOcorrencia;
import com.continuum.control.repository.AnomaliaRepository;
import com.continuum.control.repository.AutorizacaoRepository;
import com.continuum.control.repository.MissaoRepository;
import com.continuum.control.repository.OcorrenciaTemporalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HistoricoAnomaliaServiceTest {

    private AnomaliaRepository anomaliaRepository;
    private MissaoRepository missaoRepository;
    private AutorizacaoRepository autorizacaoRepository;
    private OcorrenciaTemporalRepository ocorrenciaRepository;
    private HistoricoAnomaliaService service;

    @BeforeEach
    void preparar() {
        anomaliaRepository = mock(AnomaliaRepository.class);
        missaoRepository = mock(MissaoRepository.class);
        autorizacaoRepository = mock(AutorizacaoRepository.class);
        ocorrenciaRepository = mock(OcorrenciaTemporalRepository.class);
        service = new HistoricoAnomaliaService(anomaliaRepository, missaoRepository,
                autorizacaoRepository, ocorrenciaRepository);
    }

    private Anomalia anomalia() {
        Anomalia anomalia = new Anomalia();
        anomalia.setDescricao("Anomalia em Berlim");
        anomalia.setDataDeteccao(LocalDateTime.of(2026, 11, 13, 9, 0));
        anomalia.setRisco(RiscoAnomalia.ALTO);
        return anomalia;
    }

    @Test
    void historicoOrdenadoPorData() {
        Anomalia anomalia = anomalia();
        Missao missao = mock(Missao.class);
        when(missao.getId()).thenReturn(7L);
        when(missao.getEncerradaEm()).thenReturn(LocalDateTime.of(2026, 11, 13, 11, 0));
        when(missao.getSituacao()).thenReturn(SituacaoMissao.CONCLUIDA);
        when(missao.getResumo()).thenReturn("Linha temporal estabilizada");
        Autorizacao autorizacao = new Autorizacao();
        autorizacao.setInstante(LocalDateTime.of(2026, 11, 13, 9, 30));
        autorizacao.setResponsavel("Supervisor");
        OcorrenciaTemporal ocorrencia = new OcorrenciaTemporal();
        ocorrencia.setMissaoId(7L);
        ocorrencia.setInstante(LocalDateTime.of(2026, 11, 13, 10, 0));
        ocorrencia.setTipo(TipoOcorrencia.EVIDENCIA_TEMPORAL);
        ocorrencia.setDescricao("Registro encontrado");
        ocorrencia.setDetalhes(Map.of("confiabilidade", 90));
        when(anomaliaRepository.findById(1L)).thenReturn(Optional.of(anomalia));
        when(missaoRepository.findByAnomaliaId(1L)).thenReturn(List.of(missao));
        when(autorizacaoRepository.findByMissaoId(7L)).thenReturn(List.of(autorizacao));
        when(ocorrenciaRepository.findByAnomaliaIdOrderByInstanteAsc(1L)).thenReturn(List.of(ocorrencia));

        List<HistoricoItemResponse> historico = service.buscar(1L);

        assertEquals(4, historico.size());
        assertEquals("DETECCAO", historico.get(0).getTipo());
        assertEquals("AUTORIZACAO", historico.get(1).getTipo());
        assertEquals("OCORRENCIA_EVIDENCIA_TEMPORAL", historico.get(2).getTipo());
        assertEquals("ENCERRAMENTO", historico.get(3).getTipo());
    }

    @Test
    void impedirHistoricoDeAnomaliaInexistente() {
        when(anomaliaRepository.findById(99L)).thenReturn(Optional.empty());
        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.buscar(99L));
        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }
}
