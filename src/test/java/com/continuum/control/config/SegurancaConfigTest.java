package com.continuum.control.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SegurancaConfigTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void semLoginRetorna401() throws Exception {
        mvc.perform(get("/eventos")).andExpect(status().isUnauthorized());
    }

    @Test
    void senhaIncorretaRetorna401() throws Exception {
        mvc.perform(get("/eventos").with(httpBasic("operador", "senha-errada")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void operadorPodeConsultarAgentes() throws Exception {
        mvc.perform(get("/agentes").with(httpBasic("operador", "operador123")))
                .andExpect(status().isOk());
    }

    @Test
    void agenteNaoPodeIniciarMissao() throws Exception {
        mvc.perform(patch("/missoes/2/iniciar").with(httpBasic("AG-B8-01", "agente123")))
                .andExpect(status().isForbidden());
    }

    @Test
    void operadorNaoPodeAutorizarMissao() throws Exception {
        mvc.perform(patch("/missoes/2/autorizar").with(httpBasic("operador", "operador123"))
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void supervisorNaoPodeCadastrarAgente() throws Exception {
        mvc.perform(post("/agentes").with(httpBasic("supervisor", "supervisor123"))
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void administradorPodeAcessarCadastroDeAgente() throws Exception {
        mvc.perform(post("/agentes").with(httpBasic("administrador", "admin123"))
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }
}
