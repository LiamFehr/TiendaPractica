package com.Ejemplo.tienda.Dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductoCrearDTO {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private Double precio;
    
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
    
    @Size(max = 255, message = "La URL de imagen no puede exceder 255 caracteres")
    private String imagenUrl;
    
    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    private String categoria;
    
    private String codigoInterno; // Opcional, si no se proporciona se genera automáticamente
} 