package com.koroFoods.qualificationService.service;

import org.springframework.stereotype.Service;

import com.koroFoods.qualificationService.feign.UsuarioFeign;
import com.koroFoods.qualificationService.feign.UsuarioFeignClient;
import com.koroFoods.qualificationService.feign.UsuarioPublicoDTO;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioClientService {

    private final UsuarioFeignClient usuarioFeignClient;
    private final UsuarioCacheService usuarioCacheService;

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackUsuarioPublico")
    public UsuarioPublicoDTO obtenerUsuarioPublicoConCache(Integer id) {
        System.out.println(">>> Llamando a userService para usuario: " + id);
        UsuarioPublicoDTO usuario = usuarioFeignClient.getUserByIdNoauth(id).getData();
        usuarioCacheService.guardarUsuarioPublico(id, usuario);
        System.out.println(">>> Guardado en Redis: usuarioPublico:" + id);
        return usuario;
    }

    public UsuarioPublicoDTO fallbackUsuarioPublico(Integer id, Throwable ex) {
        System.out.println(">>> FALLBACK activado: " + ex.getMessage());
        UsuarioPublicoDTO cached = usuarioCacheService.obtenerUsuarioPublico(id);
        System.out.println(">>> Redis devolvió: " + cached);
        if (cached == null) {
            throw new RuntimeException("Usuario no disponible ni en cache");
        }
        return cached;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackUsuario")
    public UsuarioFeign obtenerUsuarioConCache(Integer id) {
        UsuarioFeign usuario = usuarioFeignClient.getUsuarioById(id).getData();
        usuarioCacheService.guardarUsuario(id, usuario);
        return usuario;
    }

    public UsuarioFeign fallbackUsuario(Integer id, Throwable ex) {
        System.out.println(">>> FALLBACK auth activado: " + ex.getMessage());
        UsuarioFeign cached = usuarioCacheService.obtenerUsuario(id);
        if (cached == null) {
            throw new RuntimeException("Usuario no disponible ni en cache");
        }
        return cached;
    }
}
