package com.koroFoods.eventService.service;

import com.koroFoods.eventService.dtos.EventResponse;
import com.koroFoods.eventService.dtos.EventResquest;
import com.koroFoods.eventService.dtos.EventoDtoFeign;
import com.koroFoods.eventService.dtos.EventoFeignReserva;
import com.koroFoods.eventService.dtos.ReporteEventoItem;
import com.koroFoods.eventService.dtos.ResultadoResponse;
import com.koroFoods.eventService.dtos.TematicResponse;
import com.koroFoods.eventService.exception.BusinessException;
import com.koroFoods.eventService.exception.ResourceNotFoundException;
import com.koroFoods.eventService.feign.IMesaFeignClient;
import com.koroFoods.eventService.feign.IReservaFeignClient;
import com.koroFoods.eventService.feign.MesaFeign;
import com.koroFoods.eventService.model.Evento;
import com.koroFoods.eventService.model.EventoMesa;
import com.koroFoods.eventService.model.Tematica;
import com.koroFoods.eventService.repository.IEventoMesaRepository;
import com.koroFoods.eventService.repository.IEventoRepository;
import com.koroFoods.eventService.repository.ITematicaRepository;

import feign.Response;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventoService {

	private final IEventoRepository eventoRepository;

	private final ITematicaRepository tematicaRepository;

	private final IEventoMesaRepository eventoMesaRepository;

	private final IMesaFeignClient mesaClient;
	private final IReservaFeignClient reservaFeignClient;
	private final CloudinaryService cloudinaryService;
	
	@Autowired
	private PdfEventosService pdfEventosService; 

		@Transactional
	public EventResponse crear(EventResquest request) {
		validarFechaFutura(request.getFechaInicio());

		Evento evento = new Evento();
		evento.setNombre(request.getNombre());
		evento.setDescripcion(request.getDescripcion());
		evento.setFechaInicio(request.getFechaInicio());
		evento.setFechaFin(request.getFechaFin());
		evento.setCosto(request.getCosto());
		evento.setActivo(true);

		if (request.getImagenBase64() != null && !request.getImagenBase64().isEmpty()) {
			try {
				String url = cloudinaryService.subirImagen(
					request.getImagenBase64(),
					"korofood/eventos",
					"evento_" + System.currentTimeMillis()
				);
				evento.setImagen(url);
			} catch (IOException e) {
				throw new RuntimeException("Error al subir imagen: " + e.getMessage());
			}
		}

		if (request.getIdTematica() != null) {
			Tematica tematica = tematicaRepository.findByIdTematicaAndActivoTrue(request.getIdTematica())
				.orElseThrow(() -> new ResourceNotFoundException("Temática no encontrada"));
			evento.setTematica(tematica);
		}

		return mapearAResponse(eventoRepository.save(evento));
	}

	@Transactional(readOnly = true)
	public List<EventResponse> listarTodos() {
		return eventoRepository.findAll().stream().map(this::mapearAResponse).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<EventResponse> listarActivos() {
		return eventoRepository.findByActivoTrue().stream().map(this::mapearAResponse).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<EventResponse> listarEventosFuturos() {
		return eventoRepository.findEventosFuturos(LocalDateTime.now()).stream().map(this::mapearAResponse)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<EventResponse> listarPorTematica(Integer idTematica) {
		return eventoRepository.findByTematica_IdTematicaAndActivoTrue(idTematica).stream().map(this::mapearAResponse)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public EventResponse buscarPorId(Integer id) {
		Evento evento = eventoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con ID: " + id));
		return mapearAResponse(evento);
	}

	@Transactional
	public EventResponse actualizar(Integer id, EventResquest request) {
		Evento evento = eventoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con ID: " + id));

		validarFechaFutura(request.getFechaInicio());

		evento.setNombre(request.getNombre());
		evento.setDescripcion(request.getDescripcion());
		evento.setFechaInicio(request.getFechaInicio());
		evento.setFechaFin(request.getFechaFin());
		evento.setCosto(request.getCosto());
		evento.setImagen(request.getImagen());

		if (request.getIdTematica() != null) {
			Tematica tematica = tematicaRepository.findByIdTematicaAndActivoTrue(request.getIdTematica()).orElseThrow(
					() -> new ResourceNotFoundException("Temática no encontrada con ID: " + request.getIdTematica()));
			evento.setTematica(tematica);
		} else {
			evento.setTematica(null);
		}

		Evento actualizado = eventoRepository.save(evento);
		return mapearAResponse(actualizado);
	}

	@Transactional
	public void eliminar(Integer id) {
		Evento evento = eventoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con ID: " + id));

		evento.setActivo(false);
		eventoRepository.save(evento);
	}

	private void validarFechaFutura(LocalDateTime fecha) {
		if (fecha.isBefore(LocalDateTime.now())) {
			throw new BusinessException("La fecha del evento debe ser futura");
		}
	}

	private EventResponse mapearAResponse(Evento evento) {
		TematicResponse tematicaResponse = null;
		if (evento.getTematica() != null) {
			tematicaResponse = TematicResponse.builder().idTematica(evento.getTematica().getIdTematica())
					.nombre(evento.getTematica().getNombre()).activo(evento.getTematica().getActivo()).build();
		}

		return EventResponse.builder().idEvento(evento.getIdEvento()).nombre(evento.getNombre())
				.descripcion(evento.getDescripcion()).tematica(tematicaResponse).fechaInicio(evento.getFechaInicio())
				.fechaFin(evento.getFechaFin()).costo(evento.getCosto()).imagen(evento.getImagen())
				.activo(evento.getActivo()).build();
	}

	public ResultadoResponse<List<EventoDtoFeign>> getAllEvents() {
		List<Evento> eventos = eventoRepository.findAll();
		List<EventoDtoFeign> dtos = eventos.stream().map(evento -> {
			EventoDtoFeign dto = new EventoDtoFeign();
			dto.setIdEvento(evento.getIdEvento());
			dto.setDescripcion(evento.getDescripcion());
			dto.setNombre(evento.getNombre());
			dto.setImagen(evento.getImagen());
			return dto;
		}).toList();
		return ResultadoResponse.success("Listado de Eventos", dtos);
	}

	// Método para el feign de la reseña
	public ResultadoResponse<EventoDtoFeign> getEventById(Integer id) {
		Evento evento = eventoRepository.findById(id).orElseThrow(() -> new RuntimeException("Evento no encontrado"));

		EventoDtoFeign dto = new EventoDtoFeign();
		dto.setIdEvento(evento.getIdEvento());
		dto.setDescripcion(evento.getDescripcion());
		dto.setNombre(evento.getNombre());
		dto.setImagen(evento.getImagen());

		return ResultadoResponse.success("Evento encontrado", dto);

	}

	// Para la reserva

	@Transactional(readOnly = true)
	public ResultadoResponse<EventoFeignReserva> buscarEventoParaReserva(Integer id) {
		Evento evento = eventoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con ID: " + id));

		EventoFeignReserva dto = mapearAEventoFeignReserva(evento);

		return ResultadoResponse.success("Evento encontrado exitosamente", dto);
	}

	public ResultadoResponse<List<EventoFeignReserva>> listarEventosDelDia() {

		LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
		LocalDateTime finHoy = inicioHoy.plusDays(1);

		List<EventoFeignReserva> lst = eventoRepository.findEventosDelDia(inicioHoy, finHoy).stream()
				.map(this::mapearAEventoFeignReserva).collect(Collectors.toList());
		;

		return ResultadoResponse.success("Se listaron los eventos del dia", lst);
	}

	private EventoFeignReserva mapearAEventoFeignReserva(Evento evento) {
		EventoFeignReserva dto = new EventoFeignReserva();
		dto.setIdEvento(evento.getIdEvento());
		dto.setNombre(evento.getNombre());
		dto.setDescripcion(evento.getDescripcion());
		dto.setTematica(evento.getTematica() != null ? evento.getTematica().getNombre() : null);
		dto.setFechaInicio(evento.getFechaInicio());
		dto.setFechaFin(evento.getFechaFin());
		dto.setAforo(calcularAforoEvento(evento.getIdEvento()));
		dto.setImagen(evento.getImagen());

		return dto;
	}

	public Integer calcularAforoEvento(Integer idEvento) {
		List<EventoMesa> eventoMesas = eventoMesaRepository.findByEvento_IdEventoAndActivoTrue(idEvento);

		if (eventoMesas.isEmpty())
			return 0;

		LocalDateTime inicio = eventoMesas.get(0).getFechaDesde();
		LocalDateTime fin = eventoMesas.get(0).getFechaHasta();

		List<Integer> idsMesas = eventoMesas.stream().map(EventoMesa::getIdMesa).collect(Collectors.toList());

		ResultadoResponse<List<MesaFeign>> mesasResponse = mesaClient.obtenerMesasPorIds(idsMesas);
		if (!mesasResponse.isValor() || mesasResponse.getData() == null) {
			throw new BusinessException("No se pudo obtener información de las mesas");
		}

		ResultadoResponse<List<Integer>> ocupadasResponse = reservaFeignClient.obtenerMesasOcupadas(idsMesas, inicio,
				fin);

		List<Integer> mesasOcupadas = (ocupadasResponse != null && ocupadasResponse.getData() != null)
				? ocupadasResponse.getData()
				: List.of();

		Set<Integer> ocupadasSet = new HashSet<>(mesasOcupadas);

		return mesasResponse.getData().stream().filter(mesa -> !ocupadasSet.contains(mesa.getIdMesa()))
				.mapToInt(MesaFeign::getCapacidad).sum();
	}
	
	public byte[] generarReporteEventos() {
	    List<Evento> eventos = eventoRepository.findAll();
	    LocalDateTime ahora = LocalDateTime.now();

	    List<ReporteEventoItem> items = eventos.stream().map(e -> {
	        ReporteEventoItem item = new ReporteEventoItem();
	        item.setIdEvento(e.getIdEvento());
	        item.setNombre(e.getNombre());
	        item.setDescripcion(e.getDescripcion());
	        item.setTematica(e.getTematica() != null ? e.getTematica().getNombre() : "-");
	        item.setFechaInicio(e.getFechaInicio());
	        item.setFechaFin(e.getFechaFin());
	        item.setCosto(e.getCosto());
	        item.setActivo(e.getActivo());

	        // Calcular estado
	        String estado;
	        if (e.getFechaInicio() != null && e.getFechaFin() != null) {
	            if (ahora.isBefore(e.getFechaInicio())) {
	                estado = "Próximo";
	            } else if (ahora.isAfter(e.getFechaFin())) {
	                estado = "Finalizado";
	            } else {
	                estado = "En curso";
	            }
	        } else {
	            estado = "Sin fecha";
	        }
	        item.setEstado(estado);

	        return item;
	    }).toList();

	    return pdfEventosService.generarReporteEventos(items);
	}

}
