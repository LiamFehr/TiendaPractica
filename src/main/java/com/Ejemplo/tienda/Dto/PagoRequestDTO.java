package com.Ejemplo.tienda.Dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class PagoRequestDTO {
    
    @NotNull(message = "El pedido es obligatorio")
    private Long pedidoId;
    
    @NotEmpty(message = "Debe seleccionar al menos un método de pago")
    private List<String> metodosPago; // ["credit_card", "debit_card", "cash"]
    
    private String descripcion;
    
    private String urlRetorno; // URL de retorno después del pago
    
    private String urlCancelacion; // URL si cancela el pago
} 