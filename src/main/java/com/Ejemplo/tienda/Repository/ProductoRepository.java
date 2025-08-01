package com.Ejemplo.tienda.Repository;

import com.Ejemplo.tienda.Models.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Buscar productos activos con paginación
    Page<Producto> findByActivoTrue(Pageable pageable);
    
    // Buscar por categoría con paginación
    Page<Producto> findByCategoriaAndActivoTrue(String categoria, Pageable pageable);
    
    // Buscar por nombre (búsqueda parcial) con paginación
    Page<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre, Pageable pageable);
    
    // Buscar por rango de precios con paginación
    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :precioMin AND :precioMax AND p.activo = true")
    Page<Producto> findByPrecioBetween(@Param("precioMin") Double precioMin, @Param("precioMax") Double precioMax, Pageable pageable);
    
    // Buscar productos con stock bajo
    @Query("SELECT p FROM Producto p WHERE p.stock <= :stockLimite AND p.activo = true")
    List<Producto> findByStockBajo(@Param("stockLimite") Integer stockLimite);
    
    // Obtener todas las categorías únicas
    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.categoria IS NOT NULL AND p.activo = true")
    List<String> findAllCategorias();
    
    // Filtros avanzados con paginación
    @Query("SELECT p FROM Producto p WHERE " +
           "(:categoria IS NULL OR p.categoria = :categoria) AND " +
           "(:precioMin IS NULL OR p.precio >= :precioMin) AND " +
           "(:precioMax IS NULL OR p.precio <= :precioMax) AND " +
           "(:nombre IS NULL OR p.nombre LIKE %:nombre%) AND " +
           "(:stockMin IS NULL OR p.stock >= :stockMin) AND " +
           "p.activo = true")
    Page<Producto> findByFiltrosAvanzados(
        @Param("categoria") String categoria,
        @Param("precioMin") Double precioMin,
        @Param("precioMax") Double precioMax,
        @Param("nombre") String nombre,
        @Param("stockMin") Integer stockMin,
        Pageable pageable
    );
    
    // Productos destacados (más vendidos o con mejor rating)
    @Query("SELECT p FROM Producto p WHERE p.activo = true ORDER BY p.precio DESC")
    Page<Producto> findProductosDestacados(Pageable pageable);
    
    // Productos por precio (ascendente/descendente)
    Page<Producto> findByActivoTrueOrderByPrecioAsc(Pageable pageable);
    Page<Producto> findByActivoTrueOrderByPrecioDesc(Pageable pageable);
    
    // Productos por nombre (ascendente/descendente)
    Page<Producto> findByActivoTrueOrderByNombreAsc(Pageable pageable);
    Page<Producto> findByActivoTrueOrderByNombreDesc(Pageable pageable);
    
    // Búsqueda por código interno (para facturador)
    Optional<Producto> findByCodigoInternoAndActivoTrue(String codigoInterno);
    
    // Búsqueda para facturador
    @Query("SELECT p FROM Producto p WHERE p.activo = true AND (p.codigoInterno LIKE %:busqueda% OR p.nombre LIKE %:busqueda% OR p.categoria LIKE %:busqueda%) ORDER BY p.nombre ASC")
    List<Producto> findByFacturadorBusqueda(@Param("busqueda") String busqueda);
    
    // Productos por categoría (usando el objeto Categoria)
    @Query("SELECT p FROM Producto p WHERE p.categoriaObj.id = :categoriaId AND p.activo = true")
    List<Producto> findByCategoriaId(@Param("categoriaId") Long categoriaId);
}
