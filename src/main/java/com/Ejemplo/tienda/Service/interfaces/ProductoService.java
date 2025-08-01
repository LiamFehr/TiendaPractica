package com.Ejemplo.tienda.Service.interfaces;

import com.Ejemplo.tienda.Dto.ProductoFiltroDTO;
import com.Ejemplo.tienda.Dto.ProductoPaginadoDTO;
import com.Ejemplo.tienda.Models.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface ProductoService {
    // Métodos básicos
    List<Producto> findAll();
    Optional<Producto> findById(Long id);
    Producto save(Producto producto);
    void deleteById(Long id);
    boolean existsById(Long id);
    
    // Métodos paginados
    ProductoPaginadoDTO findAllPaginado(int pagina, int tamanio, String ordenarPor, String direccion);
    ProductoPaginadoDTO findByCategoriaPaginado(String categoria, int pagina, int tamanio);
    ProductoPaginadoDTO findByNombrePaginado(String nombre, int pagina, int tamanio);
    ProductoPaginadoDTO findByPrecioBetweenPaginado(Double precioMin, Double precioMax, int pagina, int tamanio);
    ProductoPaginadoDTO findByFiltrosAvanzados(ProductoFiltroDTO filtros);
    ProductoPaginadoDTO findProductosDestacados(int pagina, int tamanio);
    
    // Métodos de búsqueda (sin paginación para compatibilidad)
    List<Producto> findByCategoria(String categoria);
    List<Producto> findByNombre(String nombre);
    List<Producto> findByPrecioBetween(Double precioMin, Double precioMax);
    List<Producto> findByStockBajo(Integer stockLimite);
    List<String> findAllCategorias();
    
    // Métodos de estadísticas
    long countByCategoria(String categoria);
    double getPrecioPromedio();
    double getPrecioMinimo();
    double getPrecioMaximo();
}
