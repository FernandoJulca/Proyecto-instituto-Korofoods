package com.koroFoods.userServiceSoap.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.koroFoods.userServiceSoap.model.Usuario;
import com.koroFoods.userServiceSoap.repository.IUsuarioRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final IUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        if (usuarioRepository.findByNroDoc(usuario.getNroDoc()).isPresent()) {
            throw new RuntimeException("El número de documento ya está registrado");
        }

        usuario.setClave(passwordEncoder.encode(usuario.getClave()));

        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios(Integer idRol, Boolean activo) {

        List<Integer> rolesPermitidos = Arrays.asList(2, 3);

        if (idRol != null && activo != null) {
            return usuarioRepository.findByRol_IdRolInAndActivoOrderByFechaRegistroDesc(rolesPermitidos, activo);
        }

        if (idRol != null) {
            return usuarioRepository.findByRol_IdRolInOrderByFechaRegistroDesc(rolesPermitidos);
        }

        if (activo != null) {
            return usuarioRepository.findByRol_IdRolInAndActivoOrderByFechaRegistroDesc(rolesPermitidos, activo);
        }

        return usuarioRepository.findByRol_IdRolInOrderByFechaRegistroDesc(rolesPermitidos);
    }


    public Optional<Usuario> obtenerUsuario(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    @Transactional
    public Usuario actualizarUsuario(Integer idUsuario, Usuario usuarioActualizado) {
        Usuario usuarioExistente = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar correo único (excepto el mismo usuario)
        Optional<Usuario> usuarioConCorreo = usuarioRepository.findByCorreo(usuarioActualizado.getCorreo());
        if (usuarioConCorreo.isPresent() && !usuarioConCorreo.get().getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("El correo ya está registrado por otro usuario");
        }

        // Validar número de documento único (excepto el mismo usuario)
        Optional<Usuario> usuarioConDoc = usuarioRepository.findByNroDoc(usuarioActualizado.getNroDoc());
        if (usuarioConDoc.isPresent() && !usuarioConDoc.get().getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("El número de documento ya está registrado por otro usuario");
        }

        // Actualizar campos
        usuarioExistente.setNombres(usuarioActualizado.getNombres());
        usuarioExistente.setApePaterno(usuarioActualizado.getApePaterno());
        usuarioExistente.setApeMaterno(usuarioActualizado.getApeMaterno());
        usuarioExistente.setCorreo(usuarioActualizado.getCorreo());
        usuarioExistente.setTipoDoc(usuarioActualizado.getTipoDoc());
        usuarioExistente.setNroDoc(usuarioActualizado.getNroDoc());
        usuarioExistente.setDireccion(usuarioActualizado.getDireccion());
        usuarioExistente.setTelefono(usuarioActualizado.getTelefono());
        usuarioExistente.setDistrito(usuarioActualizado.getDistrito());
        usuarioExistente.setRol(usuarioActualizado.getRol());
        usuarioExistente.setActivo(usuarioActualizado.getActivo());

        // Solo actualizar contraseña si se proporciona una nueva
        if (usuarioActualizado.getClave() != null && !usuarioActualizado.getClave().isEmpty()) {
            usuarioExistente.setClave(passwordEncoder.encode(usuarioActualizado.getClave()));
        }

        return usuarioRepository.save(usuarioExistente);
    }

    @Transactional
    public void cambiarEstadoUsuario(Integer idUsuario, Boolean activo) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setActivo(activo);
        usuarioRepository.save(usuario);
    }
}