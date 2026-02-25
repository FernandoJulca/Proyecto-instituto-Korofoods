package com.koroFoods.userServiceSoap.endPoint;

import com.koroFoods.userServiceSoap.generated.*;
import com.koroFoods.userServiceSoap.mapper.UsuarioSoapMapper;
import com.koroFoods.userServiceSoap.model.Usuario;
import com.koroFoods.userServiceSoap.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Endpoint
public class UsuarioEndPoint {

    private static final String NAMESPACE_URI = "http://koroFoods.com/usuario";

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioSoapMapper mapper;

    // ==================== CREAR USUARIO ====================
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "crearUsuarioRequest")
    @ResponsePayload
    public CrearUsuarioResponse crearUsuario(@RequestPayload CrearUsuarioRequest request) {
        CrearUsuarioResponse response = new CrearUsuarioResponse();
        Respuesta respuesta = new Respuesta();

        try {
            // Validación mínima
            if (request.getClave() == null || request.getClave().trim().isEmpty()) {
                respuesta.setExitoso(false);
                respuesta.setMensaje("La contraseña es obligatoria");
                respuesta.setCodigo("VAL_001");
                response.setRespuesta(respuesta);
                return response;
            }

            String nombreRol = obtenerNombreRol(request.getIdRol());

            Usuario usuario = mapper.crearRequestAEntidad(
                request.getNombres(),
                request.getApePaterno(),
                request.getApeMaterno(),
                request.getCorreo(),
                request.getClave(),
                request.getTipoDoc(),
                request.getNroDoc(),
                request.getDireccion(),
                request.getTelefono(),
                request.getIdDistrito(),
                request.getIdRol()
            );

            Usuario usuarioCreado = usuarioService.crearUsuario(usuario);

            respuesta.setExitoso(true);
            respuesta.setMensaje(nombreRol + " creado exitosamente");
            respuesta.setCodigo("OK_001");
            response.setRespuesta(respuesta);
            response.setUsuario(mapper.entidadASoap(usuarioCreado));

        } catch (Exception e) {
            respuesta.setExitoso(false);
            respuesta.setMensaje("Error al crear usuario: " + e.getMessage());
            respuesta.setCodigo("ERR_001");
            response.setRespuesta(respuesta);
        }

        return response;
    }


    // ==================== LISTAR USUARIOS ====================
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "listarUsuariosRequest")
    @ResponsePayload
    public ListarUsuariosResponse listarUsuarios(@RequestPayload ListarUsuariosRequest request) {
        ListarUsuariosResponse response = new ListarUsuariosResponse();

        try {
            List<Usuario> usuarios = usuarioService.listarUsuarios(
                request.getIdRol(),
                request.isActivo() != null ? request.isActivo() : null
            );

            List<com.koroFoods.userServiceSoap.generated.Usuario> usuariosSoap = usuarios.stream()
                .map(mapper::entidadASoap)
                .collect(Collectors.toList());

            response.getUsuarios().addAll(usuariosSoap);

        } catch (Exception e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }

        return response;
    }

    // ==================== OBTENER USUARIO ====================
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "obtenerUsuarioRequest")
    @ResponsePayload
    public ObtenerUsuarioResponse obtenerUsuario(@RequestPayload ObtenerUsuarioRequest request) {
        ObtenerUsuarioResponse response = new ObtenerUsuarioResponse();
        Respuesta respuesta = new Respuesta();

        try {
            Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuario(request.getIdUsuario());

            if (usuarioOpt.isPresent()) {
                respuesta.setExitoso(true);
                respuesta.setMensaje("Usuario encontrado");
                respuesta.setCodigo("OK_002");
                response.setRespuesta(respuesta);
                response.setUsuario(mapper.entidadASoap(usuarioOpt.get()));
            } else {
                respuesta.setExitoso(false);
                respuesta.setMensaje("Usuario no encontrado");
                respuesta.setCodigo("NOT_FOUND_001");
                response.setRespuesta(respuesta);
            }

        } catch (Exception e) {
            respuesta.setExitoso(false);
            respuesta.setMensaje("Error al obtener usuario: " + e.getMessage());
            respuesta.setCodigo("ERR_002");
            response.setRespuesta(respuesta);
        }

        return response;
    }

    // ==================== ACTUALIZAR USUARIO ====================
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "actualizarUsuarioRequest")
    @ResponsePayload
    public ActualizarUsuarioResponse actualizarUsuario(@RequestPayload ActualizarUsuarioRequest request) {
        ActualizarUsuarioResponse response = new ActualizarUsuarioResponse();
        Respuesta respuesta = new Respuesta();

        try {
            // Mapear usando el mapper
            Usuario usuario = mapper.actualizarRequestAEntidad(
                request.getNombres(),
                request.getApePaterno(),
                request.getApeMaterno(),
                request.getCorreo(),
                request.getClave(),
                request.getTipoDoc(),
                request.getNroDoc(),
                request.getDireccion(),
                request.getTelefono(),
                request.getIdDistrito(),
                request.getIdRol(),
                request.isActivo()
            );

            // Actualizar
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(
                request.getIdUsuario(), 
                usuario
            );

            respuesta.setExitoso(true);
            respuesta.setMensaje("Usuario actualizado exitosamente");
            respuesta.setCodigo("OK_003");
            response.setRespuesta(respuesta);
            response.setUsuario(mapper.entidadASoap(usuarioActualizado));

        } catch (Exception e) {
            respuesta.setExitoso(false);
            respuesta.setMensaje("Error al actualizar usuario: " + e.getMessage());
            respuesta.setCodigo("ERR_003");
            response.setRespuesta(respuesta);
        }

        return response;
    }
    
    // ==================== CAMBIAR ESTADO USUARIO
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "cambiarEstadoUsuarioRequest")
    @ResponsePayload
    public CambiarEstadoUsuarioResponse cambiarEstadoUsuario(
            @RequestPayload CambiarEstadoUsuarioRequest request) {
        CambiarEstadoUsuarioResponse response = new CambiarEstadoUsuarioResponse();
        Respuesta respuesta = new Respuesta();

        try {
            usuarioService.cambiarEstadoUsuario(request.getIdUsuario(), request.isActivo());

            respuesta.setExitoso(true);
            respuesta.setMensaje("Estado del usuario actualizado exitosamente");
            respuesta.setCodigo("OK_005");
            response.setRespuesta(respuesta);

        } catch (Exception e) {
            respuesta.setExitoso(false);
            respuesta.setMensaje("Error al cambiar estado: " + e.getMessage());
            respuesta.setCodigo("ERR_005");
            response.setRespuesta(respuesta);
        }

        return response;
    }
    
    private String obtenerNombreRol(Integer idRol) {
        if (idRol == null) return "Usuario";

        switch (idRol) {
            case 3:
                return "Mesero";
            case 2:
                return "Recepcionista";
            default:
                return "Usuario";
        }
    }

}
