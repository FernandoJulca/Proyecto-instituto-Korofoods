package com.koroFoods.apiGateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes().route("usuario-service",
				r -> r.path("/auth/**", "/distrito/**", "/cliente/**", "/user/feign/**", "/auth/google", "/auth/github")
						.uri("lb://userService"))
				// para el uso de webSockets
				.route("usuario-service-ws",
						r -> r.path("/ws/**", "/chat/**", "/chatbot/**").uri("lb:ws://userService"))
				.route("evento-service",
						r -> r.path("/eventos/**", "/evento/feign/**", "/evento-mesas/**", "/tematicas/**")
								.uri("lb://eventService"))
				.route("menu-service",
						r -> r.path("/menu/**", "/menu/feign/**", "/etiquetas/**", "/platos/**", "/plato-etiquetas/**")
								.uri("lb://menuService"))
				.route("user-service-soap", r -> r.path("/soap/**").uri("lb://userServiceSoap"))
				.route("qualification-service", r -> r.path("/calificacion/**").uri("lb://qualificationService"))
				.route("order-service", r -> r.path("/pedido/**", "/pedido/feign/**").uri("lb://orderService"))
				.route("reservation-service",
						r -> r.path("/reserva/**", "/reserva/feign/**", "/verificacion/**")
								.uri("lb://reservationService"))
				.route("payment-service", r -> r.path("/pago/**", "/pago/feign/**").uri("lb://paymentService"))
				.route("table-service", r -> r.path("/mesa/**", "/mesa/feign/**").uri("lb://tableService")).build();
	}

}