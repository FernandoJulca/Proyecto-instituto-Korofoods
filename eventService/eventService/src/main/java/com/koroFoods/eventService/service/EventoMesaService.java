package com.koroFoods.eventService.service;

import com.koroFoods.eventService.dtos.EventResponse;
import com.koroFoods.eventService.dtos.EventTableRequest;
import com.koroFoods.eventService.dtos.EventTableResponse;
import com.koroFoods.eventService.dtos.EventoConMesaDto;
import com.koroFoods.eventService.dtos.ResultadoResponse;
import com.koroFoods.eventService.exception.BusinessException;
import com.koroFoods.eventService.exception.ResourceNotFoundException;
import com.koroFoods.eventService.feign.IMesaFeignClient;
import com.koroFoods.eventService.feign.IReservaFeignClient;
import com.koroFoods.eventService.feign.MesaFeign;
import com.koroFoods.eventService.model.Evento;
import com.koroFoods.eventService.model.EventoMesa;
import com.koroFoods.eventService.repository.IEventoMesaRepository;
import com.koroFoods.eventService.repository.IEventoRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventoMesaService {

	private final IEventoMesaRepository eventoMesaRepository;

	private final IEventoRepository eventoRepository;
	private final IMesaFeignClient mesaFeignClient;

	private final EventoService eventoService;
	private final IReservaFeignClient reservaFeignClient;

	@Transactional
	public EventTableResponse asignarMesaAEvento(EventTableRequest request) {
		validarFechas(request);

		Evento evento = eventoRepository.findById(request.getIdEvento()).orElseThrow(
				() -> new ResourceNotFoundException("Evento no encontrado con ID: " + request.getIdEvento()));

		if (eventoMesaRepository.existeSolapamientoMesaNuevo(request.getIdMesa(), request.getFechaDesde(),
				request.getFechaHasta())) {
			throw new BusinessException("La mesa ya está asignada en el rango de fechas especificado");
		}

		EventoMesa eventoMesa = new EventoMesa();
		eventoMesa.setEvento(evento);
		eventoMesa.setIdMesa(request.getIdMesa());
		eventoMesa.setFechaDesde(request.getFechaDesde());
		eventoMesa.setFechaHasta(request.getFechaHasta());
		eventoMesa.setActivo(true);

		EventoMesa guardado = eventoMesaRepository.save(eventoMesa);
		return mapearAResponse(guardado);
	}

	@Transactional(readOnly = true)
	public List<EventTableResponse> listarTodos() {
		return eventoMesaRepository.findAll().stream().map(this::mapearAResponse).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<EventTableResponse> listarActivos() {
		return eventoMesaRepository.findByActivoTrue().stream().map(this::mapearAResponse).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<EventTableResponse> listarPorEvento(Integer idEvento) {
		return eventoMesaRepository.findByEvento_IdEventoAndActivoTrue(idEvento).stream().map(this::mapearAResponse)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<EventTableResponse> listarPorMesa(Integer idMesa) {
		return eventoMesaRepository.findByIdMesaAndActivoTrue(idMesa).stream().map(this::mapearAResponse)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public EventTableResponse buscarPorId(Integer id) {
		EventoMesa eventoMesa = eventoMesaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("EventoMesa no encontrado con ID: " + id));
		return mapearAResponse(eventoMesa);
	}

	@Transactional
	public EventTableResponse actualizar(Integer id, EventTableRequest request) {
		EventoMesa eventoMesa = eventoMesaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("EventoMesa no encontrado con ID: " + id));

		validarFechas(request);

		if (eventoMesaRepository.existeSolapamientoMesa(request.getIdMesa(), request.getFechaDesde(),
				request.getFechaHasta(), id)) {
			throw new BusinessException("La mesa ya está asignada en el rango de fechas especificado");
		}

		Evento evento = eventoRepository.findById(request.getIdEvento()).orElseThrow(
				() -> new ResourceNotFoundException("Evento no encontrado con ID: " + request.getIdEvento()));

		eventoMesa.setEvento(evento);
		eventoMesa.setIdMesa(request.getIdMesa());
		eventoMesa.setFechaDesde(request.getFechaDesde());
		eventoMesa.setFechaHasta(request.getFechaHasta());

		EventoMesa actualizado = eventoMesaRepository.save(eventoMesa);
		return mapearAResponse(actualizado);
	}

	@Transactional
	public void eliminar(Integer id) {
		EventoMesa eventoMesa = eventoMesaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("EventoMesa no encontrado con ID: " + id));

		eventoMesa.setActivo(false);
		eventoMesaRepository.save(eventoMesa);
	}

	private void validarFechas(EventTableRequest request) {
		if (request.getFechaHasta().isBefore(request.getFechaDesde())) {
			throw new BusinessException("La fecha hasta debe ser posterior a la fecha desde");
		}
	}

	// Reserva

	public boolean mesaAsignadaAlEvento(Integer idMesa, Integer idEvento, LocalDateTime desde, LocalDateTime hasta) {
		return eventoMesaRepository.mesaAsignadaAlEvento(idMesa, idEvento, desde, hasta);
	}

public ResultadoResponse<List<EventoConMesaDto>> listarMesasPorEventoParaReserva(
        Integer idEvento,
        Integer cantidadPersonas) {

    List<EventoMesa> eventosMesas = eventoMesaRepository
            .findByEvento_IdEventoAndActivoTrue(idEvento);

    if (eventosMesas.isEmpty()) {
        return ResultadoResponse.error("No se encontraron mesas disponibles para este evento");
    }

    EventResponse eventoResponse = eventoService.buscarPorId(idEvento);

    List<Integer> idsMesas = eventosMesas.stream()
            .map(EventoMesa::getIdMesa)
            .collect(Collectors.toList());

    List<Integer> mesasOcupadas = new ArrayList<>();
    ResultadoResponse<List<Integer>> ocupadasResponse = reservaFeignClient.obtenerMesasOcupadas(
            idsMesas,
            eventoResponse.getFechaInicio(),
            eventoResponse.getFechaFin()
    );

    if (ocupadasResponse != null && ocupadasResponse.getData() != null) {
        mesasOcupadas = ocupadasResponse.getData();
    }

    final Set<Integer> mesasOcupadasSet = new HashSet<>(mesasOcupadas);

    Integer capacidadRequerida = null;
    if (cantidadPersonas != null) {
        capacidadRequerida = calcularCapacidadPar(cantidadPersonas);
    }

    final Integer capacidadFinal = capacidadRequerida;

    List<EventoConMesaDto> mesasFiltradas = eventosMesas.stream()
            .map(eventoMesa -> {
                // Excluir mesas que ya tienen reserva
                if (mesasOcupadasSet.contains(eventoMesa.getIdMesa())) {
                    return null;
                }

                ResultadoResponse<MesaFeign> mesaResponse =
                        mesaFeignClient.obtenerMesaPorId(eventoMesa.getIdMesa());

                if (mesaResponse != null && mesaResponse.getData() != null) {
                    MesaFeign mesa = mesaResponse.getData();

                    if (capacidadFinal != null && mesa.getCapacidad() != capacidadFinal) {
                        return null;
                    }

                    return mapearEventoMesaReserva(eventoMesa, eventoResponse, mesa);
                }

                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    if (mesasFiltradas.isEmpty()) {
        String mensaje = cantidadPersonas != null
                ? String.format("No se encontraron mesas con capacidad para %d personas", cantidadPersonas)
                : "No se encontraron mesas disponibles";
        return ResultadoResponse.error(mensaje);
    }

    String mensaje = cantidadPersonas != null
            ? String.format("Se encontraron %d mesa(s) con capacidad para %d personas",
                    mesasFiltradas.size(), cantidadPersonas)
            : String.format("Se encontraron %d mesa(s) disponibles", mesasFiltradas.size());

    return ResultadoResponse.success(mensaje, mesasFiltradas);
}
	private Integer calcularCapacidadPar(Integer cantidadPersonas) {
		if (cantidadPersonas == null || cantidadPersonas <= 0) {
			return null;
		}

		// Si es par, devolver el mismo número
		if (cantidadPersonas % 2 == 0) {
			return cantidadPersonas;
		}

		// Si es impar, redondear al par superior
		return cantidadPersonas + 1;
	}

	private EventoConMesaDto mapearEventoMesaReserva(EventoMesa eventoMesa, EventResponse eventoResponse,
			MesaFeign mesa) {

		return EventoConMesaDto.builder().idEventoMesa(eventoMesa.getIdEventoMesa()).nombre(eventoResponse.getNombre())
				.descripcion(eventoResponse.getDescripcion()).tematica(eventoResponse.getTematica().getNombre())
				.fechaInicio(eventoResponse.getFechaInicio()).fechaFin(eventoResponse.getFechaFin())
				.imagen(eventoResponse.getImagen()).idMesa(mesa.getIdMesa()).numeroMesa(mesa.getNumeroMesa())
				.capacidad(mesa.getCapacidad()).zona(mesa.getTipo()).activo(eventoResponse.getActivo()).build();
	}

	private EventTableResponse mapearAResponse(EventoMesa eventoMesa) {
		EventResponse eventoResponse = eventoService.buscarPorId(eventoMesa.getEvento().getIdEvento());

		return EventTableResponse.builder().idEventoMesa(eventoMesa.getIdEventoMesa()).evento(eventoResponse)
				.idMesa(eventoMesa.getIdMesa()).fechaDesde(eventoMesa.getFechaDesde())
				.fechaHasta(eventoMesa.getFechaHasta()).activo(eventoMesa.getActivo()).build();
	}

}
