package com.Ejemplo.tienda.Dto;

import lombok.Data;
import java.util.List;

@Data
public class CarritoDTO {
    private List<CarritoItemDTO> items;
    private Integer totalItems;
    private Double subtotal;
    private Double total;
    private String mensaje; // Para mensajes de error o éxito
} 