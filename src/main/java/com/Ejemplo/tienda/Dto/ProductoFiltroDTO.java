package com.Ejemplo.tienda.Dto;

import lombok.Data;

@Data
public class ProductoFiltroDTO {
    private String categoria;
    private Double precioMin;
    private Double precioMax;
    private String nombre;
    private Integer stockMin;
    private String ordenarPor; // "precio", "nombre", "fecha"
    private String direccion; // "asc", "desc"
    private Integer pagina = 0;
    private Integer tamanio = 10;
} 