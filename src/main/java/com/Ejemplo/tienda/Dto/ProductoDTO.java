package com.Ejemplo.tienda.Dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private String imagenUrl;
    private String categoria;
    private String codigoInterno;
    private LocalDateTime fechaCreacion;
    private Boolean activo;
}

