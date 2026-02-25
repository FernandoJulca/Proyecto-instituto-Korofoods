package com.koroFoods.userService.controller;

import com.koroFoods.userService.dto.request.UpdatePasswordRequest;
import com.koroFoods.userService.dto.response.PerfilClienteResponse;
import com.koroFoods.userService.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cliente")
@RequiredArgsConstructor
public class ClienteController {

    private final UsuarioService usuarioService;

    @PutMapping("/update/{id}")
    public ResponseEntity<?> actulizarPass(
            @PathVariable Integer id,
            @RequestBody UpdatePasswordRequest request
            ){
        try {

            usuarioService.actualizarPassword(request, id);
            return ResponseEntity.ok(
                    "Password actualizado correctamente");


        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(Map.of("Error", "No se actualizo la contraseña"));

        }
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil(@RequestParam Integer idUsuario){

        try {
            PerfilClienteResponse perfilObtenido = usuarioService.obtenerPerfilDetallado(idUsuario);

            if (perfilObtenido == null){
                throw new RuntimeException("No se encontro ningun perfil con ese ID " + idUsuario);
            }

            return ResponseEntity.ok(perfilObtenido);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
