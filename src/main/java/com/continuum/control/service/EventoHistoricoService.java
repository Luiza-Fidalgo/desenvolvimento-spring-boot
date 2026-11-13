package com.continuum.control.service;

import com.continuum.control.dto.EventoHistoricoRequest;
import com.continuum.control.model.EventoHistorico;
import com.continuum.control.model.enums.ImportanciaEvento;
import com.continuum.control.repository.EventoHistoricoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventoHistoricoService {

    private final EventoHistoricoRepository repository;

    public EventoHistoricoService(EventoHistoricoRepository repository) {
        this.repository = repository;
    }

    public EventoHistorico cadastrar(EventoHistoricoRequest dados) {
        EventoHistorico evento = new EventoHistorico();
        preencher(evento, dados);
        return repository.save(evento);
    }

    public List<EventoHistorico> listar() {
        return repository.findAll();
    }

    public EventoHistorico buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento nao encontrado"));
    }

    public List<EventoHistorico> listarPorImportancia(ImportanciaEvento importancia) {
        return repository.findByImportancia(importancia);
    }

    public List<EventoHistorico> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio.isAfter(fim)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data inicial maior que a final");
        }
        return repository.findByDataBetween(inicio, fim);
    }

    public EventoHistorico atualizar(Long id, EventoHistoricoRequest dados) {
        EventoHistorico evento = buscar(id);
        preencher(evento, dados);
        return repository.save(evento);
    }

    private void preencher(EventoHistorico evento, EventoHistoricoRequest dados) {
        evento.setNome(dados.getNome().trim());
        evento.setDescricao(dados.getDescricao().trim());
        evento.setData(dados.getData());
        evento.setLocalizacao(dados.getLocalizacao().trim());
        evento.setImportancia(dados.getImportancia());
    }
}
