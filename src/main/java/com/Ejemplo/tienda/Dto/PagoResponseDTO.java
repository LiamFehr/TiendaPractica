package com.Ejemplo.tienda.Dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PagoResponseDTO {
    private Long id;
    private String mercadopagoId;
    private String estado;
    private String metodoPago;
    private Double monto;
    private String descripcion;
    private String urlPago;
    private String urlRetorno;
    private LocalDateTime fechaCreacion;
    private String mensaje; // Para mensajes de error o éxito
} 