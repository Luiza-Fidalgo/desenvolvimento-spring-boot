package com.continuum.control.service;

import com.continuum.control.model.Agente;
import com.continuum.control.repository.AgenteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PermissaoAgenteService {

    private final AgenteRepository agenteRepository;

    public PermissaoAgenteService(AgenteRepository agenteRepository) {
        this.agenteRepository = agenteRepository;
    }

    public void verificarRegistro(Long agenteId, Authentication usuario) {
        boolean perfilAgente = usuario.getAuthorities().stream()
                .anyMatch(permissao -> permissao.getAuthority().equals("ROLE_AGENTE"));

        // Um agente so pode registrar ocorrencias usando seu proprio codigo.
        if (perfilAgente) {
            Agente agente = agenteRepository.findById(agenteId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agente nao encontrado"));
            if (!agente.getCodigo().equals(usuario.getName())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Agente nao pode agir em nome de outro");
            }
        }
    }
}
