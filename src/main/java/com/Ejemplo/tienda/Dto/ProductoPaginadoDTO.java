package com.Ejemplo.tienda.Dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductoPaginadoDTO {
    private List<ProductoDTO> productos;
    private int paginaActual;
    private int totalPaginas;
    private long totalElementos;
    private int tamanioPagina;
    private boolean primeraPagina;
    private boolean ultimaPagina;
    private boolean tieneSiguiente;
    private boolean tieneAnterior;
} 