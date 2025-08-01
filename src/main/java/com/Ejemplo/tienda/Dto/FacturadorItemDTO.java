package com.Ejemplo.tienda.Dto;

import lombok.Data;

@Data
public class FacturadorItemDTO {
    private Long id;
    private String codigoInterno;
    private String nombre;
    private String categoria;
    private Double precio;
    private Integer stock;
    private Integer cantidad;
    private Double subtotal;
    private String imagenUrl;
} 