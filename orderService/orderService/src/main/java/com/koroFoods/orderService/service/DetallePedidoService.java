package com.koroFoods.orderService.service;

import com.koroFoods.orderService.dto.ResultadoResponse;
import com.koroFoods.orderService.dto.request.DetallePedidoRequest;
import com.koroFoods.orderService.dto.request.IncrementarStock;
import com.koroFoods.orderService.dto.response.*;
import com.koroFoods.orderService.enums.EstadoDetallePedido;
import com.koroFoods.orderService.enums.EstadoPedido;
import com.koroFoods.orderService.feign.*;
import com.koroFoods.orderService.model.DetallePedido;
import com.koroFoods.orderService.model.Pedido;
import com.koroFoods.orderService.repository.IDetallePedidoRepository;
import com.koroFoods.orderService.repository.IPedidoRepository;
import com.koroFoods.orderService.util.DashboardNotificador;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DetallePedidoService {

    private final IDetallePedidoRepository detallePedidoRepository;
    private final IPedidoRepository pedidoRepository;
    private final UsuarioFeignClient feignClient;
    private final ReservaFeignClient reservaFeignClient;
    private final PlatoFeignClient platoFeignClient;

    private final DashboardNotificador dashboardNotificador;
    //Obtenemos el cliente existente por el id_reserva que hay en pedido que a la vez relaciona una
    //reserva con un cliente
    public ResultadoResponse<DetallePedidoUsuarioResponse> obtenerUsuarioPorPedidoReserva(Integer idPedido) {

        //Validar id
        validarId(idPedido);

        //Obtener el pedido y al cliente asociado
        Pedido pedido = obtenerPedido(idPedido);
        ResultadoResponse<UsuarioFeign> usuarioCliente = obtenerClientePorReserva(pedido.getIdReserva());
        var usuarioData = usuarioCliente.getData();

        //Obtener datos de una query personalizada
        DetalleEstadoCount pedidosEstados = detallePedidoRepository.findByIdPedido(idPedido);


        DetallePedidoUsuarioResponse response = new DetallePedidoUsuarioResponse();
        response.setNombres(usuarioData.getNombres());
        response.setApePaterno(usuarioData.getApePaterno());
        response.setApeMaterno(usuarioData.getApeMaterno());
        response.setPedidos(pedidosEstados.getPedidos());
        response.setEntregados(pedidosEstados.getEntregados());
        response.setCancelados(pedidosEstados.getCancelados());

        return ResultadoResponse.success("Se obtuvo al cliente: ", response);

    }


    private Pedido obtenerPedido(Integer id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error al obtener el pedido: " + id));
    }

    public ResultadoResponse<Pedido> obtenerPedidoPorId(Integer idPedido){
        validarId(idPedido);

        Pedido obtenido = obtenerPedido(idPedido);
        if (obtenido != null){
            return ResultadoResponse.success("Se obtuvo el pedido: ", obtenido);
        }

        return  ResultadoResponse.error("No se obuto el pedido error.",null);
    }


    private ResultadoResponse<UsuarioFeign> obtenerClientePorReserva(Integer idUsuario) {
        validarId(idUsuario);
        UsuarioFeign obtenido = reservaFeignClient.obtenerUsuarioPorReserva(idUsuario).getData();

        return ResultadoResponse.success("Cliente obtenido: ", obtenido);

    }

    @Transactional
    public ResultadoResponse<DetallePedido> cambiarEstadoAEntregado(Integer idDetalle) {

        validarId(idDetalle);

        DetallePedido dp = detallePedidoRepository.findById(idDetalle)
                .orElseThrow(() -> new RuntimeException("Error al obetner el detalle: " + idDetalle));

        validarEstadoParaEntregar(dp.getEstado());

        dp.setEstado(EstadoDetallePedido.ENT);
        detallePedidoRepository.save(dp);

        return ResultadoResponse.success("Se actualizo al estado Entregado", dp);
    }


    @Transactional
    public ResultadoResponse<DetallePedidoPagar> procederAlPago(Integer idPedido){
        validarId(idPedido);

        List<DetallePedidoResponse> listaDeDetalles =
                obtenerDetallePorPedido(idPedido).getData();

        //Validamos q ningun detalle este en estado PENDIENTE
        for (var detalle : listaDeDetalles){
            if (detalle.getEstado().equals("PED")){
                throw new RuntimeException("Las ordenes no pueden estar en PENDIENTE");
            }
        }

        int totalPlatos = listaDeDetalles.stream()
                .mapToInt(DetallePedidoResponse::getCantidad)
                .sum();

        //Sumamos las cantidades
        Pedido pedido = obtenerPedido(idPedido);
        ResultadoResponse<UsuarioFeign> usuarioCliente = obtenerClientePorReserva(pedido.getIdReserva());
        var clienteData =usuarioCliente.getData();


        DetallePedidoPagar response = new DetallePedidoPagar();


        response.setIdCliente(clienteData.getIdUsuario());
        response.setNombreCliente(
                String.format(clienteData.getNombres() + " " + clienteData.getApePaterno()));
        response.setTotalPlatos(totalPlatos);
        response.setMetodoPago(null);

        BigDecimal descontar = BigDecimal.valueOf(15);
        BigDecimal total = pedido.getTotal().subtract(descontar);

        response.setTotalPagar(total);

        return ResultadoResponse.success("Proceder al pago, ",response);
    }


    @Transactional
    public ResultadoResponse<Pedido> cambiarAPagadoLaOrder(Integer idPedido){
        validarId(idPedido);

        Pedido actualizar = obtenerPedido(idPedido);
        if (actualizar != null){
            actualizar.setEstado(EstadoPedido.PA);
            pedidoRepository.save(actualizar);

            dashboardNotificador.notificarGraficoCinco(LocalDate.now().getMonthValue());
            return ResultadoResponse.success("Se pago correctamente el pedido", actualizar);

        }
        return ResultadoResponse.error("Hubo un error al pagar el pedido: " + idPedido, null);
    }

    @Transactional
    public ResultadoResponse<DetallePedido> cambiarEstadoACancelado(Integer idDetalle) {

        validarId(idDetalle);

        DetallePedido dp = detallePedidoRepository.findById(idDetalle)
                .orElseThrow(() -> new RuntimeException("Error al obetner el detalle: " + idDetalle));

        validarEstadoParaCancelar(dp.getEstado());

        dp.setEstado(EstadoDetallePedido.CAN);

        reducirSubTotalDelPedido(dp.getIdPedido(), dp.getIdDetalle());

        detallePedidoRepository.save(dp);

        return ResultadoResponse.success("Cancelaste este plato", dp);
    }

    private void validarEstadoParaEntregar(EstadoDetallePedido estado) {
        if (estado.equals(EstadoDetallePedido.CAN) || estado.equals(EstadoDetallePedido.ENT)) {
            throw new RuntimeException("El estado no puede estar cancelado para poder entregar");
        }
    }

    private void validarEstadoParaCancelar(EstadoDetallePedido estado) {
        if (estado.equals(EstadoDetallePedido.ENT) || estado.equals(EstadoDetallePedido.CAN)) {
            throw new RuntimeException("El estado no puede estar entregado para poder cancelar");
        }
    }

    private void reducirSubTotalDelPedido(Integer idPedido, Integer idDetalle){

        validarId(idPedido);
        validarId(idDetalle);

        //Obtenemos las entidades a actualizar
        DetallePedido dpObtenido = obtenerDetallePedido(idDetalle);
        Pedido pedidoObtenido = obtenerPedido(idPedido);

        BigDecimal subTotalDetalle = dpObtenido.getSubtotal();

        BigDecimal nuevoSubTotal = pedidoObtenido.getSubTotal().subtract(subTotalDetalle);
        BigDecimal nuevoTotal = pedidoObtenido.getTotal().subtract(subTotalDetalle);

        aumentarStockDelPlatoCancelado(idDetalle);

        pedidoObtenido.setSubTotal(nuevoSubTotal);
        pedidoObtenido.setTotal(nuevoTotal);
        pedidoRepository.save(pedidoObtenido);

    }

    private void aumentarStockDelPlatoCancelado(Integer idDetalle){
        validarId(idDetalle);

        DetallePedido pd = obtenerDetallePedido(idDetalle);

        IncrementarStock request = new IncrementarStock();

        request.setIdPlato(pd.getIdPlato());
        request.setCantidad(pd.getCantidad());

        platoFeignClient.incrementarStock(request);
    }

    private DetallePedido obtenerDetallePedido(Integer idDetalle){
        validarId(idDetalle);
        return detallePedidoRepository.findById(idDetalle)
                .orElseThrow(() -> new RuntimeException("Detalle de pedido no encontrado"));
    }

    @Transactional
    public ResultadoResponse<DetallePedido> registrarPlato(DetallePedidoRequest request) {

        //Validamos
        validarRequest(request);

        //Obtenemos pedido y plato
        Pedido actualizarPedido = obtenerPedido(request.getIdPedido());
        var platoData = obtnerPlatoValidar(request.getIdPlato(), request.getCantidad());

        //Registramos el plato a un pedido existente
        DetallePedido registrar = crearDetallePedido(request, platoData);
        detallePedidoRepository.save(registrar);

        //Actualizamos el stock del plato los subtotales etc
        actualizarStockPlato(request.getIdPlato(), request.getCantidad());
        actualizarTotalesPedido(actualizarPedido, registrar.getSubtotal());

        //Mandamos la notificacion al DASHBOARD para que le llegue la data
        //que solicita para actualizar automaticamente el grafico UNO*
        dashboardNotificador.notificarGraficoUno(LocalDate.now().getMonthValue());


        return ResultadoResponse.success("Se agrego un nuevo plato a tu orden  " + platoData.getNombre(), registrar);
    }


    private void validarRequest(DetallePedidoRequest request) {
        if (request == null) {
            throw new RuntimeException("Request Null");
        }
    }

    private PlatoFeign obtnerPlatoValidar(Integer idPlato, Integer cantidad) {
        var plato = platoFeignClient.getDishById(idPlato);

        if (plato.getData() == null) {
            throw new RuntimeException("Error al obtner el plato" + idPlato);
        }

        var platoData = plato.getData();
        validarStockDisponible(platoData, cantidad);

        return platoData;
    }

    private void validarStockDisponible(PlatoFeign plato, Integer cantidad) {
        if (cantidad > plato.getStock()) {
            throw new RuntimeException(
                    String.format("Cantidad seleccionada (%d) supera al stock disponible (%d)",
                            cantidad, plato.getStock())
            );
        }
    }

    private DetallePedido crearDetallePedido(DetallePedidoRequest request, PlatoFeign plato) {
        BigDecimal subtotal = calcularSubtotal(plato.getPrecio(), request.getCantidad());

        DetallePedido detalle = new DetallePedido();
        detalle.setIdPedido(request.getIdPedido());
        detalle.setIdPlato(request.getIdPlato());
        detalle.setCantidad(request.getCantidad());
        detalle.setPrecioUnitario(plato.getPrecio());
        detalle.setSubtotal(subtotal);
        detalle.setEstado(EstadoDetallePedido.PED);

        return detalle;
    }

    private BigDecimal calcularSubtotal(BigDecimal precioUnitario, Integer cantidad) {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    private void actualizarStockPlato(Integer idPlato, Integer cantidad) {
        platoFeignClient.substractStockOrder(idPlato, cantidad);
    }

    private void actualizarTotalesPedido(Pedido pedido, BigDecimal nuevoSubtotal) {
        pedido.setSubTotal(pedido.getSubTotal().add(nuevoSubtotal));
        pedido.setTotal(pedido.getTotal().add(nuevoSubtotal));
        pedidoRepository.save(pedido);
    }


    public ResultadoResponse<List<DetallePedidoResponse>> obtenerDetallePorPedido(Integer pedidoId) {

        validarId(pedidoId);

        List<DetallePedido> detalles =
                detallePedidoRepository.findByIdPedidoDescEstado(pedidoId);
        List<DetallePedidoResponse> response =
                detalles
                        .stream()
                        .map(this::mapToResponse)
                        .toList();
        return ResultadoResponse.success("Lista Obtenida! ", response);

    }

    private DetallePedidoResponse mapToResponse(DetallePedido dp) {

        var platoObtenido = platoFeignClient.getDishById(dp.getIdPlato());
        var platoData = platoObtenido.getData();
        var pedido = obtenerPedido(dp.getIdPedido());

        DetallePedidoResponse rs = new DetallePedidoResponse();

        rs.setIdReserva(pedido.getIdReserva());
        rs.setIdDetalle(dp.getIdDetalle());
        rs.setIdPedido(dp.getIdPedido());
        rs.setIdPlato(dp.getIdPlato());

        rs.setImagen(platoData.getImagen());
        rs.setNombre(platoData.getNombre());

        rs.setCantidad(dp.getCantidad());
        rs.setEstado(dp.getEstado().toString());
        rs.setPrecioUnitario(dp.getPrecioUnitario());
        rs.setSubTotal(dp.getSubtotal());
        return rs;
    }




    public ResultadoResponse<DetallePedidoMeseroResponse>obtenerUsuarioPorPedido(Integer idPedido){

        validarId(idPedido);

        Pedido pedido = obtenerPedido(idPedido);

        ResultadoResponse<UsuarioFeign> usuario = feignClient.getUsuarioById(pedido.getIdUsuario());
        var usuarioData = usuario.getData();

        DetallePedidoMeseroResponse meseroResponse = obtenerDetallePedidoMesero(usuarioData);

        return ResultadoResponse.success("Mesero obtenido: ", meseroResponse);
    }

    private DetallePedidoMeseroResponse obtenerDetallePedidoMesero(UsuarioFeign usuarioFeign){

        DetallePedidoMeseroResponse response = new DetallePedidoMeseroResponse();
        response.setNombres(usuarioFeign.getNombres());
        response.setApePaterno(usuarioFeign.getApePaterno());
        response.setApeMaterno(usuarioFeign.getApeMaterno());

        DetalleCantidadPedidos cantidadPedidos = pedidoRepository.obtenerCantidadDePedidos(usuarioFeign.getIdUsuario());

        response.setPedidosTotales(cantidadPedidos.getPedidosTotales());
        response.setClientesTotales(cantidadPedidos.getClientesTotales());
        response.setPedidosCompletados(cantidadPedidos.getPedidosCompletados());

        return response;
    }

    private void validarId(Integer request) {
        if (request == null || request <= 0) {
            log.error("Error al obtener con ID: {}", request);
            throw new IllegalArgumentException("ID invalido");
        }
    }

    public ResultadoResponse<List<GraficoUnoListResponse>> grafico1(Integer mes){

        List<GraficoUnoData> data = detallePedidoRepository.graficoUnoList(mes);
        List<GraficoUnoListResponse> list = new ArrayList<>();
        for(var plato : data){
            ResultadoResponse<PlatoFeign> plat = platoFeignClient.getDishById(plato.getIdPlato());
            var platoData = plat.getData();

            list.add(new GraficoUnoListResponse(
                    plato.getIdPlato(),
                    plato.getCantidadPlatos(),
                    platoData.getNombre()
            ));
        }

        return ResultadoResponse.success("Se obtuvo el grafico 1: ", list);
    }


}
