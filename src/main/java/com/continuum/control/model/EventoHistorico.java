package com.continuum.control.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.continuum.control.model.enums.ImportanciaEvento;

@Entity
@Table(name = "eventos_historicos")
public class EventoHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 2000)
    private String descricao;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false, length = 150)
    private String localizacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ImportanciaEvento importancia;

    public EventoHistorico() {
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public ImportanciaEvento getImportancia() {
        return importancia;
    }

    public void setImportancia(ImportanciaEvento importancia) {
        this.importancia = importancia;
    }
}
