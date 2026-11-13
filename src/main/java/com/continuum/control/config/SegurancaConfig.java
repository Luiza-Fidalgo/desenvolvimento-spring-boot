package com.continuum.control.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SegurancaConfig {

    @Bean
    public SecurityFilterChain filtro(HttpSecurity http) throws Exception {
        http
            // A API e usada por clientes HTTP sem formularios e sem sessao de navegador.
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(acesso -> acesso
                .requestMatchers("/error").permitAll()
                // Consultas podem ser feitas por qualquer perfil autenticado.
                .requestMatchers(HttpMethod.GET, "/**").authenticated()

                // Cadastro e alteracao dos registros administrativos.
                .requestMatchers(HttpMethod.POST, "/agentes", "/eventos").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PUT, "/agentes/**", "/eventos/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PATCH, "/agentes/**").hasRole("ADMINISTRADOR")

                // Operadores podem registrar e analisar anomalias.
                .requestMatchers(HttpMethod.POST, "/anomalias")
                    .hasAnyRole("OPERADOR", "SUPERVISOR", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.PATCH, "/anomalias/*/analisar")
                    .hasAnyRole("OPERADOR", "SUPERVISOR", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.PUT, "/anomalias/**")
                    .hasAnyRole("SUPERVISOR", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.PATCH, "/anomalias/**")
                    .hasAnyRole("SUPERVISOR", "ADMINISTRADOR")

                // Agentes registram ocorrencias; a identidade e conferida no servico.
                .requestMatchers(HttpMethod.POST, "/missoes/*/ocorrencias")
                    .hasAnyRole("AGENTE", "SUPERVISOR", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.POST, "/missoes")
                    .hasAnyRole("OPERADOR", "SUPERVISOR", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.PUT, "/missoes/**")
                    .hasAnyRole("SUPERVISOR", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.PATCH, "/missoes/**")
                    .hasAnyRole("SUPERVISOR", "ADMINISTRADOR")
                .anyRequest().denyAll()
            )
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder codificador() {
        return new BCryptPasswordEncoder();
    }

    // Usuarios demonstrativos: as senhas e o login do agente vem da configuracao.
    @Bean
    public UserDetailsService usuarios(
            PasswordEncoder codificador,
            @Value("${app.security.operador.password}") String senhaOperador,
            @Value("${app.security.agente.login}") String loginAgente,
            @Value("${app.security.agente.password}") String senhaAgente,
            @Value("${app.security.supervisor.password}") String senhaSupervisor,
            @Value("${app.security.administrador.password}") String senhaAdministrador) {
        return new InMemoryUserDetailsManager(
            User.withUsername("operador").password(codificador.encode(senhaOperador)).roles("OPERADOR").build(),
            User.withUsername(loginAgente).password(codificador.encode(senhaAgente)).roles("AGENTE").build(),
            User.withUsername("supervisor").password(codificador.encode(senhaSupervisor)).roles("SUPERVISOR").build(),
            User.withUsername("administrador").password(codificador.encode(senhaAdministrador)).roles("ADMINISTRADOR").build()
        );
    }
}
