package com.Ejemplo.tienda.Repository;

import com.Ejemplo.tienda.Models.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    
    List<CarritoItem> findByUsuarioIdOrderByFechaAgregadoDesc(Long usuarioId);
    
    Optional<CarritoItem> findByUsuarioIdAndProductoId(Long usuarioId, Long productoId);
    
    @Query("SELECT ci FROM CarritoItem ci WHERE ci.usuario.id = :usuarioId AND ci.producto.activo = true ORDER BY ci.fechaAgregado DESC")
    List<CarritoItem> findActiveItemsByUsuario(@Param("usuarioId") Long usuarioId);
    
    void deleteByUsuarioId(Long usuarioId);
    
    void deleteByUsuarioIdAndProductoId(Long usuarioId, Long productoId);
} 