package com.Ejemplo.tienda.Service.interfaces;

import com.Ejemplo.tienda.Dto.PagoRequestDTO;
import com.Ejemplo.tienda.Dto.PagoResponseDTO;

public interface MercadoPagoService {
    
    PagoResponseDTO crearPago(PagoRequestDTO request);
    
    PagoResponseDTO obtenerPago(String mercadopagoId);
    
    PagoResponseDTO actualizarEstadoPago(String mercadopagoId, String estado);
    
    void procesarWebhook(String mercadopagoId, String estado);
} 