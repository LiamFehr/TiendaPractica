package com.Ejemplo.tienda.Repository;

import com.Ejemplo.tienda.Models.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    
    Optional<Pago> findByMercadopagoId(String mercadopagoId);
    
    Optional<Pago> findByPedidoId(Long pedidoId);
    
    List<Pago> findByEstado(String estado);
    
    List<Pago> findByPedidoUsuarioId(Long usuarioId);
} 