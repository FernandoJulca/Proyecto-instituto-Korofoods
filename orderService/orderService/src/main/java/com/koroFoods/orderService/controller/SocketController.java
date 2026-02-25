package com.koroFoods.orderService.controller;

import com.koroFoods.orderService.dto.ResultadoResponse;
import com.koroFoods.orderService.dto.request.DetallePedidoRequest;
import com.koroFoods.orderService.dto.response.DetallePedidoMeseroResponse;
import com.koroFoods.orderService.dto.response.DetallePedidoPagar;
import com.koroFoods.orderService.dto.response.DetallePedidoResponse;
import com.koroFoods.orderService.dto.response.DetallePedidoUsuarioResponse;
import com.koroFoods.orderService.model.DetallePedido;
import com.koroFoods.orderService.service.DetallePedidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final DetallePedidoService detallePedidoService;


    @MessageMapping("/pedido/{idPedido}/agregar")
    public void agregarPlato(
            @DestinationVariable Integer idPedido,
            @Payload DetallePedidoRequest request
    ) {
        try {
            log.info("MENSAJE RECIBIDO - Pedido: {}, Request: {}", idPedido, request);
            log.info("Cliente agregando plato al pedido: {}", idPedido);

            //Registramos el plato
            ResultadoResponse<DetallePedido> dResponse = detallePedidoService.registrarPlato(request);

            if (dResponse.isValor()) {
                log.info("✅ Plato registrado exitosamente");
                //Obtener la lista actualizada para la visualización del mesero y el cliente tambien
                ResultadoResponse<List<DetallePedidoResponse>> detallesActualizados =
                        detallePedidoService.obtenerDetallePorPedido(idPedido);

                //Obtener la informacion actualizada del mesero para mostrar al cliente
                ResultadoResponse<DetallePedidoMeseroResponse> infoMesero =
                        detallePedidoService.obtenerUsuarioPorPedido(idPedido);

                //Obtener la informacion actualizada del cliente para mostrar al mesero
                ResultadoResponse<DetallePedidoUsuarioResponse> infoCliente =
                        detallePedidoService.obtenerUsuarioPorPedidoReserva(idPedido);

                //Notificar al cliente (ve a mesero)
                messagingTemplate.convertAndSend(
                        "/topic/pedido/" + idPedido + "/cliente",
                        Map.of(
                                "detalles", detallesActualizados.getData(),
                                "infoMesero", infoMesero.getData()
                        )
                );

                //Notificar al Mesero ( ve al cliente)
                messagingTemplate.convertAndSend(
                        "/topic/pedido/" + idPedido + "/mesero",
                        Map.of(
                                "detalles", detallesActualizados.getData(),
                                "infoCliente", infoCliente.getData()
                        )
                );

                log.info("Plato agregao exitosamente!");
            }
        } catch (Exception e) {
            log.error("Error al agregar plato: ", e);
            messagingTemplate.convertAndSend(
                    "/topic/pedido/" + idPedido + "/error",
                    Map.of("mensaje", e.getMessage())
            );
        }
    }

    @MessageMapping("/pedido/{idPedido}/entregar")
    public void entregarPlato(
            @DestinationVariable Integer idPedido,
            @Payload Integer idDetalle
    ) {
        try {
            log.info("Mesero entregando plato {} del pedido {}", idDetalle, idPedido);

            //Cambiamos el detalle_pedido a entregado
            ResultadoResponse<DetallePedido> response =
                    detallePedidoService.cambiarEstadoAEntregado(idDetalle);

            if (response.isValor()) {

                //Obtener la lista actualizada
                ResultadoResponse<List<DetallePedidoResponse>> detallesActualizado =
                        detallePedidoService.obtenerDetallePorPedido(idPedido);

                //Obtener la informacion actualizada del mesero para mostrar al cliente
                ResultadoResponse<DetallePedidoMeseroResponse> infoMesero =
                        detallePedidoService.obtenerUsuarioPorPedido(idPedido);

                //Obtener la informacion actualizada del cliente para mostrar al mesero
                ResultadoResponse<DetallePedidoUsuarioResponse> infoCliente =
                        detallePedidoService.obtenerUsuarioPorPedidoReserva(idPedido);

                //Notificar al Cliente ( ve al mesero)
                messagingTemplate.convertAndSend(
                        "/topic/pedido/" + idPedido + "/cliente",
                        Map.of(
                                "detalles", detallesActualizado.getData(),
                                "infoMesero", infoMesero.getData()
                        )
                );

                //Notificar al Mesero ( ve al cliente)
                messagingTemplate.convertAndSend(
                        "/topic/pedido/" + idPedido + "/mesero",
                        Map.of(
                                "detalles", detallesActualizado.getData(),
                                "infoCliente", infoCliente.getData()
                        )
                );

                log.info("Plato entregao {} del pedido {}", idDetalle, idPedido);
            }

        } catch (Exception e) {
            log.error("Error al entregar plato: ", e);
            messagingTemplate.convertAndSend(
                    "/topic/pedido/" + idPedido + "/error",
                    Map.of("mensaje", e.getMessage())
            );
        }
    }

    @MessageMapping("/pedido/{idPedido}/cancelar")
    public void cancelarPlato(
            @DestinationVariable Integer idPedido,
            @Payload Integer idDetalle
    ) {
        try {

            log.info("Cancelando plato {}, del pedido {}", idDetalle, idPedido);

            //Cambiar a estado cancelado el plato
            ResultadoResponse<DetallePedido> resultado =
                    detallePedidoService.cambiarEstadoACancelado(idDetalle);
            if (resultado.isValor()) {

                //Obtener la lista actualizada
                ResultadoResponse<List<DetallePedidoResponse>> detallesActualizado =
                        detallePedidoService.obtenerDetallePorPedido(idPedido);

                //Obtener la informacion actualizada del mesero para mostrar al cliente
                ResultadoResponse<DetallePedidoMeseroResponse> infoMesero =
                        detallePedidoService.obtenerUsuarioPorPedido(idPedido);

                //Obtener la informacion actualizada del cliente para mostrar al mesero
                ResultadoResponse<DetallePedidoUsuarioResponse> infoCliente =
                        detallePedidoService.obtenerUsuarioPorPedidoReserva(idPedido);

                messagingTemplate.convertAndSend(
                        "/topic/pedido/" + idPedido + "/cliente",
                        Map.of(
                                "detalles", detallesActualizado.getData(),
                                "infoMesero", infoMesero.getData()
                        )
                );

                //Notificar al Mesero ( ve al cliente)
                messagingTemplate.convertAndSend(
                        "/topic/pedido/" + idPedido + "/mesero",
                        Map.of(
                                "detalles", detallesActualizado.getData(),
                                "infoCliente", infoCliente.getData()
                        )
                );

                log.info("Plato cancelado ");
            }

        } catch (Exception e) {
            log.error("Error al cancelar plato: ", e);
            messagingTemplate.convertAndSend(
                    "/topic/pedido/" + idPedido + "/error",
                    Map.of("mensaje", e.getMessage())
            );
        }
    }

    @MessageMapping("/pedido/{idPedido}/pagar")
    public void pagarPedido(
            @DestinationVariable Integer idPedido
    ) {
        try {
            ResultadoResponse<DetallePedidoPagar>response =
                    detallePedidoService.procederAlPago(idPedido);

            if (response.isValor()) {
                messagingTemplate.convertAndSend(
                        "/topic/pedido/" + idPedido + "/mesero",
                        Map.of("inicioPago", true, "pago", response.getData())
                );
                messagingTemplate.convertAndSend(
                        "/topic/pedido/" + idPedido + "/cliente",
                        Map.of("inicioPago", true, "pago", response.getData())
                );
            }
        } catch (Exception e) {
            messagingTemplate.convertAndSend(
                    "/topic/pedido/" + idPedido + "/error",
                    Map.of("mensaje", e.getMessage())
            );
        }

    }


    @MessageMapping("/pedido/{idPedido}/elegirMetodo")
    public void elegirMetodo(
            @DestinationVariable Integer idPedido,
            @Payload String metodoPago
    ) {
        try {
            String metodoLimpio = metodoPago.replace("\"", "").trim();

            messagingTemplate.convertAndSend(
                    "/topic/pedido/" + idPedido + "/mesero",
                    Map.of("metodoPago", metodoLimpio)
            );
        } catch (Exception e) {
            messagingTemplate.convertAndSend(
                    "/topic/pedido/" + idPedido + "/error",
                    Map.of("mensaje", e.getMessage())
            );
        }
    }


    @MessageMapping("/pedido/{idPedido}/confirmarPago")
    public void confirmarPago(
            @DestinationVariable Integer idPedido,
            @Payload String metodoPago
    ) {
        try {
            String metodoLimpio = metodoPago.replace("\"", "").trim();

            messagingTemplate.convertAndSend(
                    "/topic/pedido/" + idPedido + "/mesero",
                    Map.of("pagoConfirmado", true, "metodoPago", metodoLimpio)
            );
            messagingTemplate.convertAndSend(
                    "/topic/pedido/" + idPedido + "/cliente",
                    Map.of("pagoConfirmado", true, "metodoPago", metodoLimpio)
            );
        } catch (Exception e) {
            messagingTemplate.convertAndSend(
                    "/topic/pedido/" + idPedido + "/error",
                    Map.of("mensaje", e.getMessage())
            );
        }
    }
}
