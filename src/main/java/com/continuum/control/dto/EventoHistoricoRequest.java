package com.continuum.control.dto;

import com.continuum.control.model.enums.ImportanciaEvento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class EventoHistoricoRequest {

    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 150)
    private String nome;

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 2000)
    private String descricao;

    @NotNull(message = "Data e obrigatoria")
    private LocalDate data;

    @NotBlank(message = "Localizacao e obrigatoria")
    @Size(max = 150)
    private String localizacao;

    @NotNull(message = "Importancia e obrigatoria")
    private ImportanciaEvento importancia;

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
