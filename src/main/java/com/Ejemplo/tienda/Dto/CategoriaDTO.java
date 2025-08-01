package com.Ejemplo.tienda.Dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoriaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String color;
    private Integer orden;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private Integer totalProductos; // Contador de productos en esta categoría
} 