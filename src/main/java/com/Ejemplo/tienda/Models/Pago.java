package com.Ejemplo.tienda.Models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Pago {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
    
    @Column(nullable = false)
    private String mercadopagoId; // ID de Mercado Pago
    
    @Column(nullable = false)
    private String estado; // pending, approved, rejected, cancelled
    
    private String metodoPago; // credit_card, debit_card, etc.
    
    @Column(nullable = false)
    private Double monto;
    
    private String descripcion;
    
    private LocalDateTime fechaCreacion;
    
    private LocalDateTime fechaActualizacion;
    
    private String urlPago; // URL para redirigir al usuario
    
    private String urlRetorno; // URL de retorno después del pago
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
} 