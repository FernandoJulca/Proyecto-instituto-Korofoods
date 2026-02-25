package com.koroFoods.userService.controller;

import com.koroFoods.userService.dto.GithubUserDto;

import com.koroFoods.userService.dto.GoogleUserDto;
import com.koroFoods.userService.dto.request.RegistroSocialRequest;
import com.koroFoods.userService.dto.response.SocialAuthResponse;
import com.koroFoods.userService.service.GoogleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.koroFoods.userService.dto.request.LoginRequest;
import com.koroFoods.userService.model.Usuario;
import com.koroFoods.userService.service.CloudinaryService;
import com.koroFoods.userService.service.GithubService;
import com.koroFoods.userService.service.UsuarioService;
import com.koroFoods.userService.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.koroFoods.userService.dto.ResultadoResponse;
import com.koroFoods.userService.dto.UsuarioDtoFeign;
import com.koroFoods.userService.dto.UsuarioPerfilDTO;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CloudinaryService cloudinaryService;
    private final GithubService githubService;
    private final GoogleService googleService;


    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody LoginRequest loginRequest) {

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getCorreo(),
                            loginRequest.getClave()));

            UserDetails userDetails = (UserDetails) auth.getPrincipal();

            List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            String token = jwtUtil.generateToken(loginRequest.getCorreo(), roles);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            System.out.println(">>> ERROR EN AUTENTICACIÓN: " + e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
    }

    @PostMapping(value = "/register", consumes = { "multipart/form-data" })
    public ResponseEntity<ResultadoResponse<?>> registrarUsuario(@ModelAttribute Usuario usuario) {
        try {
            if (usuario.getImagenMultipart() != null && !usuario.getImagenMultipart().isEmpty()) {
                String urlImagen = cloudinaryService.uploadImage(usuario.getImagenMultipart(), "KoroFoods/Users");
                usuario.setImagen(urlImagen);
            }

            ResultadoResponse<Usuario> resultado = usuarioService.registrarUsuario(usuario);
            if (resultado.isValor()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
            }

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ResultadoResponse.error("Error registrando usuario: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> obtnerUsuario(@RequestHeader("X-Username") String correoCliente) {
        Optional<Usuario> usuarioObtenido = usuarioService.obtenerDatosCliente(correoCliente);

        if (usuarioObtenido.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioObtenido.get();
        return ResponseEntity.ok(usuario);
    }

    //Login/Register con Github
    @PostMapping("/github")
    public ResponseEntity<?> githubLogin(@RequestParam String code) {
        try{
            GithubUserDto githubUserDto = githubService.loginWithGithub(code);

            SocialAuthResponse response = usuarioService.verificarUsuarioRegistrado(
                    githubUserDto.getName(),
                    githubUserDto.getEmail(),
                    githubUserDto.getAvatar(),
                    "github"
            );
            System.out.println("✅ [CONTROLLER] Response generado: " + response);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("¡Error' ", "Error en autenticación GitHub: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
	public ResponseEntity<ResultadoResponse<UsuarioPerfilDTO>> getUserById(@PathVariable Integer id) {
		ResultadoResponse<UsuarioPerfilDTO> user = usuarioService.getUsuarioById(id);
		return ResponseEntity.ok(user);
	}
    
    //Login/Register con Google
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload){
        try {

            String idToken = payload.get("idToken");

            if (idToken == null || idToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Token de Google requerido"));
            }

            GoogleUserDto googleUserDto = googleService.verifyGoogleToken(idToken);


            SocialAuthResponse response = usuarioService.verificarUsuarioRegistrado(
                    googleUserDto.getName(),
                    googleUserDto.getEmail(),
                    googleUserDto.getAvatar(),
                    "google"
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("¡Error' ", "Error en autenticación Google: " + e.getMessage()));
        }
    }

    //Registrar el cliente completo ya sea con google o con GitHub
    @PostMapping("/social/register")
    public ResponseEntity<?>registrarSocialGitHubGoogle(
            @RequestBody RegistroSocialRequest request
            ){
        try {

            ResultadoResponse<?> resultado = usuarioService.registraUsuarioSocial(request);
            if (resultado.isValor()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ResultadoResponse.error("Error completando registro: " + e.getMessage()));
        }
    }

}
