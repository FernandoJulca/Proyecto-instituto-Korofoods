package com.koroFoods.apiGateway.util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/login",
            "/auth/register",
            "/distrito/list",
            "/auth/github",
            "/auth/google",
            "/auth/social/register",
            "/cliente/index",
            "/evento/feign/**",
            "/menu/feign",
            "/menu",
            "/calificacion",
            "/user/feign/noauth/**",
            "/ws/**",
            "/pago/feign"
        );

    @Override
    public int getOrder() {
        return 1; 
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        String method = request.getMethod().toString();
        
        System.out.println("=================================================");
        System.out.println("GATEWAY RECIBIÓ PETICIÓN");
        System.out.println("Path: " + path);
        System.out.println("Método: " + method);
        System.out.println("Origin: " + request.getHeaders().getFirst("Origin"));
        System.out.println("=================================================");
        
        if ("OPTIONS".equalsIgnoreCase(method)) {
            System.out.println("Preflight OPTIONS - dejando pasar al CorsFilter");
            exchange.getResponse().setStatusCode(HttpStatus.OK);
            return exchange.getResponse().setComplete();
        }
        
        if (isPublicPath(path)) {
            System.out.println("Ruta pública, dejando pasar sin validar JWT");
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Token no encontrado o formato incorrecto");
            return onError(exchange, "No autorizado: falta token", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        if (token.isBlank()) {
            System.out.println("Token vacío");
            return onError(exchange, "Token JWT no proporcionado", HttpStatus.UNAUTHORIZED);
        }

        try {
            if (!jwtUtil.validarToken(token)) {
                System.out.println("Token inválido");
                return onError(exchange, "Token JWT inválido o expirado", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            System.out.println("Error validando token: " + e.getMessage());
            return onError(exchange, "Token JWT inválido o mal formado", HttpStatus.UNAUTHORIZED);
        }

        String username = jwtUtil.obtenerUsuarioAndToken(token);
        System.out.println("Token válido para usuario: " + username);
        
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
            .header("X-Username", username)
            .build();

        System.out.println("Reenviando petición con header X-Username: " + username);

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream()
                .anyMatch(publicPath -> path.startsWith(publicPath.replace("/**", "")));
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        String errorJson = String.format("{\"error\": \"%s\"}", message);
        return exchange.getResponse().writeWith(
            Mono.just(exchange.getResponse().bufferFactory().wrap(errorJson.getBytes()))
        );
    }
}

