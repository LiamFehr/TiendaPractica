package com.Ejemplo.tienda.Repository;

import com.Ejemplo.tienda.Models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    Optional<Categoria> findByNombre(String nombre);
    
    List<Categoria> findByActivoTrueOrderByOrdenAsc();
    
    @Query("SELECT c FROM Categoria c WHERE c.activo = true AND (c.nombre LIKE %:busqueda% OR c.descripcion LIKE %:busqueda%) ORDER BY c.orden ASC")
    List<Categoria> findByBusqueda(@Param("busqueda") String busqueda);
    
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.categoriaObj.id = :categoriaId AND p.activo = true")
    Long countProductosByCategoria(@Param("categoriaId") Long categoriaId);
    
    boolean existsByNombre(String nombre);
} 