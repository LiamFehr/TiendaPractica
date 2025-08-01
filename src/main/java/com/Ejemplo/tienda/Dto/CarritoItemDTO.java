package com.Ejemplo.tienda.Dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CarritoItemDTO {
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private String imagenUrl;
    private Double precio;
    private Integer cantidad;
    private Double subtotal;
    private LocalDateTime fechaAgregado;
} 