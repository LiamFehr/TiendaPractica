package com.Ejemplo.tienda.Service.impl;

import com.Ejemplo.tienda.Dto.PagoRequestDTO;
import com.Ejemplo.tienda.Dto.PagoResponseDTO;
import com.Ejemplo.tienda.Models.Pago;
import com.Ejemplo.tienda.Models.Pedido;
import com.Ejemplo.tienda.Repository.PagoRepository;
import com.Ejemplo.tienda.Repository.PedidoRepository;
import com.Ejemplo.tienda.Service.interfaces.MercadoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class MercadoPagoServiceImpl implements MercadoPagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Value("${mercadopago.access-token:test-token}")
    private String accessToken;

    @Value("${mercadopago.public-key:test-public-key}")
    private String publicKey;

    @Override
    public PagoResponseDTO crearPago(PagoRequestDTO request) {
        // Buscar el pedido
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(request.getPedidoId());
        if (!pedidoOpt.isPresent()) {
            throw new RuntimeException("Pedido no encontrado");
        }

        Pedido pedido = pedidoOpt.get();

        // Crear pago en la base de datos
        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMercadopagoId("MP-" + UUID.randomUUID().toString().substring(0, 8));
        pago.setEstado("pending");
        pago.setMonto(pedido.getTotal());
        pago.setDescripcion(request.getDescripcion() != null ? request.getDescripcion() : "Pago de pedido #" + pedido.getId());
        pago.setUrlRetorno(request.getUrlRetorno());

        // Simular integración con Mercado Pago
        // En producción, aquí se haría la llamada real a la API de Mercado Pago
        String urlPago = "https://www.mercadopago.com/checkout/v1/redirect?pref_id=" + pago.getMercadopagoId();
        pago.setUrlPago(urlPago);

        Pago savedPago = pagoRepository.save(pago);

        return convertirADTO(savedPago);
    }

    @Override
    public PagoResponseDTO obtenerPago(String mercadopagoId) {
        Optional<Pago> pagoOpt = pagoRepository.findByMercadopagoId(mercadopagoId);
        if (!pagoOpt.isPresent()) {
            throw new RuntimeException("Pago no encontrado");
        }
        return convertirADTO(pagoOpt.get());
    }

    @Override
    public PagoResponseDTO actualizarEstadoPago(String mercadopagoId, String estado) {
        Optional<Pago> pagoOpt = pagoRepository.findByMercadopagoId(mercadopagoId);
        if (!pagoOpt.isPresent()) {
            throw new RuntimeException("Pago no encontrado");
        }

        Pago pago = pagoOpt.get();
        pago.setEstado(estado);
        pago.setFechaActualizacion(LocalDateTime.now());

        // Si el pago fue aprobado, actualizar el estado del pedido
        if ("approved".equals(estado)) {
            Pedido pedido = pago.getPedido();
            pedido.setEstado("pagado");
            pedidoRepository.save(pedido);
        }

        Pago savedPago = pagoRepository.save(pago);
        return convertirADTO(savedPago);
    }

    @Override
    public void procesarWebhook(String mercadopagoId, String estado) {
        actualizarEstadoPago(mercadopagoId, estado);
    }

    private PagoResponseDTO convertirADTO(Pago pago) {
        PagoResponseDTO dto = new PagoResponseDTO();
        dto.setId(pago.getId());
        dto.setMercadopagoId(pago.getMercadopagoId());
        dto.setEstado(pago.getEstado());
        dto.setMetodoPago(pago.getMetodoPago());
        dto.setMonto(pago.getMonto());
        dto.setDescripcion(pago.getDescripcion());
        dto.setUrlPago(pago.getUrlPago());
        dto.setUrlRetorno(pago.getUrlRetorno());
        dto.setFechaCreacion(pago.getFechaCreacion());
        return dto;
    }
} 