package com.continuum.control.config;

import com.continuum.control.model.Agente;
import com.continuum.control.model.Anomalia;
import com.continuum.control.model.EventoHistorico;
import com.continuum.control.model.enums.ImportanciaEvento;
import com.continuum.control.model.enums.RiscoAnomalia;
import com.continuum.control.repository.AgenteRepository;
import com.continuum.control.repository.AnomaliaRepository;
import com.continuum.control.repository.EventoHistoricoRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@Profile("dev")
public class DadosIniciaisConfig {

    // Carga simples de exemplo. So executa quando o profile dev estiver ativo.
    @Bean
    public ApplicationRunner dadosIniciais(AgenteRepository agentes,
                                           EventoHistoricoRepository eventos,
                                           AnomaliaRepository anomalias) {
        return args -> {
            if (agentes.count() == 0) {
                Agente agente = new Agente();
                agente.setCodigo("AG-B8-01");
                agente.setNome("Carlos Silva");
                agente.setEspecialidade("Analise temporal");
                agentes.save(agente);
            }

            if (eventos.count() == 0) {
                EventoHistorico evento = new EventoHistorico();
                evento.setNome("Queda do Muro de Berlim");
                evento.setDescricao("Evento historico monitorado pela agencia");
                evento.setData(LocalDate.of(1989, 11, 9));
                evento.setLocalizacao("Berlim");
                evento.setImportancia(ImportanciaEvento.ALTA);
                eventos.save(evento);
            }

            if (anomalias.count() == 0) {
                EventoHistorico evento = eventos.findAll().stream().findFirst().orElse(null);
                if (evento != null) {
                    Anomalia anomalia = new Anomalia();
                    anomalia.setEvento(evento);
                    anomalia.setDescricao("Alteracao inesperada na linha temporal");
                    anomalia.setDataDeteccao(LocalDateTime.now());
                    anomalia.setRisco(RiscoAnomalia.MODERADO);
                    anomalias.save(anomalia);
                }
            }
        };
    }
}
