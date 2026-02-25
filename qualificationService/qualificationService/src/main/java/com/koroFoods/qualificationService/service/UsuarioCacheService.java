package com.koroFoods.qualificationService.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.koroFoods.qualificationService.feign.UsuarioFeign;
import com.koroFoods.qualificationService.feign.UsuarioPublicoDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final long TTL = 60;

    public void guardarUsuario(Integer id, UsuarioFeign usuario) {
        redisTemplate.opsForValue().set("usuario:" + id, usuario, TTL, TimeUnit.MINUTES);
    }

    public UsuarioFeign obtenerUsuario(Integer id) {
        return (UsuarioFeign) redisTemplate.opsForValue().get("usuario:" + id);
    }

    public void guardarUsuarioPublico(Integer id, UsuarioPublicoDTO usuario) {
        redisTemplate.opsForValue().set("usuarioPublico:" + id, usuario, TTL, TimeUnit.MINUTES);
    }

    public UsuarioPublicoDTO obtenerUsuarioPublico(Integer id) {
        Object value = redisTemplate.opsForValue().get("usuarioPublico:" + id);
        System.out.println(">>> Valor en Redis: " + value + " tipo: " + (value != null ? value.getClass() : "null"));
        
        if (value instanceof UsuarioPublicoDTO dto) {
            return dto;
        }
        
        if (value instanceof java.util.LinkedHashMap<?, ?> map) {
            UsuarioPublicoDTO dto = new UsuarioPublicoDTO();
            dto.setIdUsuario((Integer) map.get("idUsuario"));
            dto.setNombreCompleto((String) map.get("nombreCompleto"));
            dto.setImagen((String) map.get("imagen"));
            return dto;
        }
        
        return null;
    }
}