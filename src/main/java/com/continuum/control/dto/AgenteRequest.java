package com.continuum.control.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AgenteRequest {

    @NotBlank(message = "Codigo e obrigatorio")
    @Size(max = 30, message = "Codigo deve ter no maximo 30 caracteres")
    private String codigo;

    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 100, message = "Nome deve ter no maximo 100 caracteres")
    private String nome;

    @NotBlank(message = "Especialidade e obrigatoria")
    @Size(max = 100, message = "Especialidade deve ter no maximo 100 caracteres")
    private String especialidade;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
}
