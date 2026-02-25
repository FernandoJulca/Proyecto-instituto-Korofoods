package com.koroFoods.qualificationService.service;

import com.koroFoods.qualificationService.dto.ResenaListResponse;
import com.koroFoods.qualificationService.dto.ResenaRequest;
import com.koroFoods.qualificationService.dto.ResultadoResponse;
import com.koroFoods.qualificationService.dto.response.GraficoSeisData;
import com.koroFoods.qualificationService.dto.response.GraficoSeisList;
import com.koroFoods.qualificationService.enums.EstadoResena;
import com.koroFoods.qualificationService.enums.TipoEntidad;
import com.koroFoods.qualificationService.feign.*;
import com.koroFoods.qualificationService.model.Calificacion;
import com.koroFoods.qualificationService.repository.ICalificacionRepository;

import com.koroFoods.qualificationService.util.DashboardNotificador;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalificacionService {
    private final ICalificacionRepository resenaRepository;
    private final PlatoFeignClient platoFeignClient;
    private final EventoFeignClient eventoFeignClient;
    private final UsuarioClientService usuarioClientService;

    private final DashboardNotificador dashboardNotificador;

    public ResultadoResponse<List<ResenaListResponse>> listarResenas() {
        List<Calificacion> list = resenaRepository.findAll();

        List<ResenaListResponse> listResponse = list.stream()
                .map(resena -> convertirAResenaListResponse(resena, true))
                .collect(Collectors.toList());

        return ResultadoResponse.success("Reseñas listadas correctamente", listResponse);
    }

    public ResultadoResponse<List<ResenaListResponse>> obtenerResenasPorUsuario(Integer idUsuario) {
        List<Calificacion> resenas = resenaRepository.findByIdUsuario(idUsuario);

        List<ResenaListResponse> listResponse = resenas.stream()
                .map(resena -> convertirAResenaListResponse(resena, false))
                .collect(Collectors.toList());

        return ResultadoResponse.success("Reseñas del usuario listadas correctamente", listResponse);
    }

    public ResultadoResponse<Calificacion> crearResena(ResenaRequest req) {
        try {
            usuarioClientService.obtenerUsuarioConCache(req.getIdUsuario());
        } catch (FeignException.NotFound e) {
            return ResultadoResponse.error("El usuario con ID " + req.getIdUsuario() + " no existe");
        } catch (Exception e) {
            return ResultadoResponse.error("Usuario no disponible: " + e.getMessage());
        }

        switch (req.getTipoEntidad()) {
            case PLATO -> {
                try {
                    platoFeignClient.getDishById(req.getIdEntidad());
                } catch (FeignException.NotFound e) {
                    return ResultadoResponse.error("El plato con ID " + req.getIdEntidad() + " no existe");
                } catch (FeignException e) {
                    return ResultadoResponse.error("Error al consultar el plato: " + e.getMessage());
                }
            }
            case EVENTO -> {
                try {
                    eventoFeignClient.getEventById(req.getIdEntidad());
                } catch (FeignException.NotFound e) {
                    return ResultadoResponse.error("El evento con ID " + req.getIdEntidad() + " no existe");
                } catch (FeignException e) {
                    return ResultadoResponse.error("Error al consultar el evento: " + e.getMessage());
                }
            }
        }

        boolean yaExiste = resenaRepository.existsByIdUsuarioAndTipoEntidadAndIdEntidad(
                req.getIdUsuario(), req.getTipoEntidad(), req.getIdEntidad());

        if (yaExiste) {
            return ResultadoResponse.error("Ya enviaste una calificación sobre este " +
                    (req.getTipoEntidad() == TipoEntidad.PLATO ? "plato" : "evento"));
        }

        Calificacion r = new Calificacion();
        r.setIdUsuario(req.getIdUsuario());
        r.setTipoEntidad(req.getTipoEntidad());
        r.setIdEntidad(req.getIdEntidad());
        r.setPuntuacion(req.getCalificacion());
        r.setComentario(req.getComentario());
        r.setFechaRegistro(LocalDateTime.now());
        r.setEstado(EstadoResena.ACT);

        resenaRepository.save(r);

        if (r.getTipoEntidad().equals(TipoEntidad.PLATO)){
            dashboardNotificador.notificarGraficoSeis(LocalDate.now().getMonthValue());
        }
        return ResultadoResponse.success("Reseña registrada correctamente", r);
    }

    private ResenaListResponse convertirAResenaListResponse(Calificacion resena, boolean publico) {
        ResenaListResponse response = new ResenaListResponse();
        response.setIdResena(resena.getIdCalificacion());
        response.setIdUsuario(resena.getIdUsuario());
        response.setIdEntidad(resena.getIdEntidad());
        response.setCalificacion(resena.getPuntuacion());
        response.setComentario(resena.getComentario());

        try {
            if (publico) {
                UsuarioPublicoDTO usuario = usuarioClientService.obtenerUsuarioPublicoConCache(resena.getIdUsuario());
                response.setNombreUsuarioCompleto(usuario.getNombreCompleto());
                response.setImagenUsuario(usuario.getImagen());
            } else {
                UsuarioFeign usuario = usuarioClientService.obtenerUsuarioConCache(resena.getIdUsuario());
                response.setNombreUsuarioCompleto(
                        usuario.getNombres() + " " +
                                usuario.getApePaterno() + " " +
                                usuario.getApeMaterno());
                response.setImagenUsuario(usuario.getImagen());
            }
        } catch (Exception e) {
            System.err.println("Usuario no disponible ni en cache: " + e.getMessage());
            response.setNombreUsuarioCompleto(publico ? "Usuario anónimo" : "Usuario no disponible");
            response.setImagenUsuario(null);
        }

        // entidades sin cambios
        try {
            switch (resena.getTipoEntidad()) {
                case PLATO -> {
                    var plato = platoFeignClient.getDishById(resena.getIdEntidad());
                    if (plato != null && plato.getData() != null) {
                        response.setImagenEntidad(plato.getData().getImagen());
                        response.setNombreEntidad(plato.getData().getNombre());
                    }
                }
                case EVENTO -> {
                    var evento = eventoFeignClient.getEventById(resena.getIdEntidad());
                    if (evento != null && evento.getData() != null) {
                        response.setImagenEntidad(evento.getData().getImagen());
                        response.setNombreEntidad(evento.getData().getNombre());
                    }
                }
            }
        } catch (FeignException e) {
            System.err.println("Error al obtener entidad: " + e.getMessage());
            response.setImagenEntidad(null);
            response.setNombreEntidad("Entidad no disponible");
        }

        return response;
    }

    public ResultadoResponse<List<GraficoSeisList>> graficoSeisList(Integer mes) {

        List<GraficoSeisData> data = resenaRepository.graficoSeisList(mes);
        List<GraficoSeisList> list = new ArrayList<>();

        for (var plato : data) {
            ResultadoResponse<PlatoFeign> platoFeign = platoFeignClient.getDishById(plato.getIdEntidad());
            var platoData = platoFeign.getData();

            list.add(new GraficoSeisList(
                    plato.getIdEntidad(),
                    plato.getPromedio(),
                    plato.getTotal(),
                    platoData.getNombre()));

        }

        if (!list.isEmpty()) {
            return ResultadoResponse.success("Se obtuvo la lista: ", list);
        }

        return ResultadoResponse.error("No hay datos para la lista", list);
    }
}
