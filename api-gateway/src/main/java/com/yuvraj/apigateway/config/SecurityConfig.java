package com.yuvraj.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String MANAGER = "FLEET_MANAGER";
    private static final String DISPATCHER = "DISPATCHER";
    private static final String SAFETY = "SAFETY_OFFICER";
    private static final String FINANCE = "FINANCIAL_ANALYST";

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        // browser preflight requests carry no token; the gateway's CORS config answers them
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // vehicles
                        .pathMatchers(HttpMethod.GET, "/api/vehicles/**")
                        .hasAnyRole(ADMIN, MANAGER, DISPATCHER, SAFETY, FINANCE)
                        .pathMatchers("/api/vehicles/**").hasAnyRole(ADMIN, MANAGER)

                        // drivers
                        .pathMatchers(HttpMethod.GET, "/api/drivers/**")
                        .hasAnyRole(ADMIN, MANAGER, DISPATCHER, SAFETY, FINANCE)
                        .pathMatchers("/api/drivers/**").hasAnyRole(ADMIN, SAFETY)

                        // trips
                        .pathMatchers(HttpMethod.GET, "/api/trips/**")
                        .hasAnyRole(ADMIN, MANAGER, DISPATCHER, SAFETY, FINANCE)
                        .pathMatchers("/api/trips/**").hasAnyRole(ADMIN, DISPATCHER)

                        // maintenances
                        .pathMatchers(HttpMethod.GET, "/api/maintenances/**")
                        .hasAnyRole(ADMIN, MANAGER, FINANCE)
                        .pathMatchers("/api/maintenances/**").hasAnyRole(ADMIN, MANAGER)

                        // expenses
                        .pathMatchers(HttpMethod.GET, "/api/expenses/**")
                        .hasAnyRole(ADMIN, MANAGER, FINANCE)
                        .pathMatchers("/api/expenses/**").hasAnyRole(ADMIN, FINANCE)

                        // reports
                        .pathMatchers(HttpMethod.GET, "/api/reports/**")
                        .hasAnyRole(ADMIN, MANAGER, FINANCE)

                        // anything else is blocked
                        .anyExchange().denyAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakRoleConverter())));

        return http.build();
    }

    // Keycloak puts roles in realm_access.roles, which Spring ignores by default.
    // This reads them and adds the ROLE_ prefix so hasRole("ADMIN") works.
    private ReactiveJwtAuthenticationConverter keycloakRoleConverter() {
        Converter<Jwt, Flux<GrantedAuthority>> authoritiesConverter = jwt -> {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null || realmAccess.get("roles") == null) {
                return Flux.empty();
            }
            Collection<String> roles = (List<String>) realmAccess.get("roles");
            return Flux.fromIterable(roles)
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role));
        };
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }
}