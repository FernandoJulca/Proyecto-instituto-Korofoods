package com.koroFoods.userService.service;

import com.koroFoods.userService.dto.ResultadoResponse;
import com.koroFoods.userService.dto.SocialUserDataDto;
import com.koroFoods.userService.dto.UsuarioDtoFeign;
import com.koroFoods.userService.dto.UsuarioPerfilDTO;
import com.koroFoods.userService.dto.UsuarioPublicoDTO;
import com.koroFoods.userService.dto.request.RegistroSocialRequest;
import com.koroFoods.userService.dto.request.UpdatePasswordRequest;
import com.koroFoods.userService.dto.response.PerfilClienteResponse;
import com.koroFoods.userService.dto.response.SocialAuthResponse;
import com.koroFoods.userService.model.Distrito;
import com.koroFoods.userService.model.Rol;
import com.koroFoods.userService.model.Usuario;
import com.koroFoods.userService.repository.IUsuarioRepository;
import com.koroFoods.userService.util.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService  {

    private final IUsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    public Optional<Usuario> obtenerDatosCliente(String correo){
        return  usuarioRepository.findByCorreo(correo);
    }


    public ResultadoResponse<Usuario> registrarUsuario(Usuario usuario) {

        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            return ResultadoResponse.error("El correo ingresado ya existe, elige otro");
        }

        if (usuarioRepository.findByTelefono(usuario.getTelefono()).isPresent()) {
            return ResultadoResponse.error("El teléfono ingresado ya existe, elige otro");
        }

        if (usuarioRepository.findByNroDoc(usuario.getNroDoc()).isPresent()) {
            return ResultadoResponse.error("El N°: " + usuario.getNroDoc() + " ya fue registrado, elige otro");
        }

        Rol rolDefinido = new Rol();
        rolDefinido.setIdRol(4);

        usuario.setClave(bCryptPasswordEncoder.encode(usuario.getClave()));
        usuario.setRol(rolDefinido);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);

        Usuario nuevoUsuario = usuarioRepository.save(usuario);

        return ResultadoResponse.success("El usuario fue creado correctamente", nuevoUsuario);
    }

    public ResultadoResponse<UsuarioPerfilDTO> getUsuarioById(Integer id){
    	Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    	UsuarioPerfilDTO dto = new UsuarioPerfilDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombres(usuario.getNombres());
        dto.setApePaterno(usuario.getApePaterno());
        dto.setApeMaterno(usuario.getApeMaterno());
        dto.setCorreo(usuario.getCorreo());
        dto.setTelefono(usuario.getTelefono());
        dto.setImagen(usuario.getImagen());
        dto.setDireccion(usuario.getDireccion());
        dto.setTipoDoc(usuario.getTipoDoc().toString());
        dto.setNroDoc(usuario.getNroDoc());
        dto.setDistrito(usuario.getDistrito().getNombre());
        dto.setFechaRegistro(usuario.getFechaRegistro().toString());
        return ResultadoResponse.success("Usuario encontrado", dto);
    }
    
    @Cacheable(value = "usuarios", key = "#id")
    public ResultadoResponse<UsuarioDtoFeign> getUsuarioByIdFeign(Integer id){
    	Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UsuarioDtoFeign dto = new UsuarioDtoFeign();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombres(usuario.getNombres());
        dto.setApePaterno(usuario.getApePaterno());
        dto.setApeMaterno(usuario.getApeMaterno());
        dto.setCorreo(usuario.getCorreo());
        dto.setTelefono(usuario.getTelefono());
        dto.setImagen(usuario.getImagen());
        return ResultadoResponse.success("Usuario encontrado", dto);
    }
    
    @Cacheable(value = "usuariosPublicos", key = "#id")
    public ResultadoResponse<UsuarioPublicoDTO> getUsuarioByIdPublic(Integer id){
    	Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->  new RuntimeException("Usuario no encontrado"));

    	UsuarioPublicoDTO dto = new UsuarioPublicoDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombreCompleto(usuario.getNombres() +" " +
                usuario.getApePaterno() + " " +
                usuario.getApeMaterno());
        dto.setImagen(usuario.getImagen());

        return ResultadoResponse.success("Usuario encontrado", dto);
    }
    
    public void actualizarPassword(UpdatePasswordRequest request, Integer id){

        Usuario usuarioEncontrado = usuarioRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Usuario no encontrado")
        );

        if (!bCryptPasswordEncoder.matches(
                request.getPasswordActual(),
                usuarioEncontrado.getClave()
        )){
            throw new RuntimeException("La contraseña actual es incorrecto");
        };

        if (bCryptPasswordEncoder.matches(
                request.getPasswordNuevo(),
                usuarioEncontrado.getClave()
        )){
            throw new RuntimeException("La contraseña nueva no puede ser igual a la anterior");
        }


        usuarioEncontrado.setClave(
                bCryptPasswordEncoder.encode(request.getPasswordNuevo())
        );

        usuarioRepository.save(usuarioEncontrado);

    }

    public PerfilClienteResponse obtenerPerfilDetallado (Integer id ){

        return usuarioRepository.obtenerPerfil(id);
    }


    public SocialAuthResponse verificarUsuarioRegistrado(
            String nombre,
            String email,
            String avatar,
            String provider
    ){

        Optional<Usuario> usuarioEncontado = usuarioRepository.findByCorreo(email);

        //Usuario  encontrado genera token y finaliza la validacion
        if (usuarioEncontado.isPresent()){

            Usuario usuario = usuarioEncontado.get();

            List<String> roles = List.of(usuario.getRol().getDescripcion());
            String token = jwtUtil.generateToken( email ,roles);

            SocialAuthResponse response = new SocialAuthResponse();
            response.setUsuarioExistente(true);
            response.setToken(token);
            response.setSocialUserDataDto(null);
            response.setTempToken(null);

            return response;

        }
        //Si no existe un usurio q intenta logearse o registrarse con github tiene q completar el registro
        //con los datos que esta respondiendo ya sea google/github
        else{

            String tempToken =  generarTokenTemporal(email, nombre, avatar, provider);

            SocialUserDataDto userData = new SocialUserDataDto();
            userData.setNombre(nombre);
            userData.setEmail(email);
            userData.setAvatar(avatar);
            userData.setProvider(provider);

            SocialAuthResponse response = new SocialAuthResponse();
            response.setUsuarioExistente(false);
            response.setToken(null);
            response.setTempToken(tempToken);
            response.setSocialUserDataDto(userData);

            return response;
        }
    }

    public ResultadoResponse<?> registraUsuarioSocial(RegistroSocialRequest request){
        try {

            if (usuarioRepository.findByCorreo(request.getCorreo()).isPresent()){
                return ResultadoResponse.error("El correo ya esta registrado intente otro");
            }

            if (usuarioRepository.findByTelefono(request.getTelefono()).isPresent()) {
                return ResultadoResponse.error("El teléfono ya está registrado");
            }

            if (usuarioRepository.findByNroDoc(request.getNroDoc()).isPresent()) {
                return ResultadoResponse.error("El número de documento ya está registrado");
            }

            Usuario nuevoUsuario = new Usuario();

            nuevoUsuario.setNombres(request.getNombres());
            nuevoUsuario.setImagen(request.getImagen());

            nuevoUsuario.setCorreo(request.getCorreo());
            nuevoUsuario.setApePaterno(request.getApePaterno());
            nuevoUsuario.setApeMaterno(request.getApeMaterno());
            nuevoUsuario.setTipoDoc(request.getTipoDocumento());
            nuevoUsuario.setNroDoc(request.getNroDoc());
            nuevoUsuario.setTelefono(request.getTelefono());
            nuevoUsuario.setDireccion(request.getDireccion());

            Distrito distritoEncontrado = new Distrito();
            distritoEncontrado.setIdDistrito(request.getIdDistrito());
            nuevoUsuario.setDistrito(distritoEncontrado);

            Rol rolPorDefeto = new Rol();
            rolPorDefeto.setIdRol(4);
            nuevoUsuario.setRol(rolPorDefeto);

            nuevoUsuario.setClave(bCryptPasswordEncoder.encode(request.getClave()));

            nuevoUsuario.setFechaRegistro(LocalDateTime.now());
            nuevoUsuario.setActivo(true);

            Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

            if (usuarioGuardado.getRol() == null) {
                usuarioGuardado = usuarioRepository.findById(usuarioGuardado.getIdUsuario())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado después de guardar"));
            }

            String rolDescripcion = "C";
            if (usuarioGuardado.getRol() != null && usuarioGuardado.getRol().getDescripcion() != null) {
                rolDescripcion = usuarioGuardado.getRol().getDescripcion();
            }

            List<String> roles = List.of(rolDescripcion);
            String token = jwtUtil.generateToken(request.getCorreo(), roles);

            Map<String, Object> data = Map.of(
                    "token", token,
                    "usuario", usuarioGuardado
            );

            return ResultadoResponse.success(
                    "Usuario registrado exitosamente", data
            );


        } catch (Exception e) {
            throw new RuntimeException("Error al completar el registro: " + e.getMessage());
        }
    }


    public String generarTokenTemporal(String email, String nombre, String avatar, String provider){
        return UUID.randomUUID().toString() + ":" + email + ":" + provider;
    }




}
