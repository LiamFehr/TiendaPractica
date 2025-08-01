package com.Ejemplo.tienda.Service.interfaces;

import com.Ejemplo.tienda.Dto.CategoriaDTO;
import com.Ejemplo.tienda.Models.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriaService {
    
    List<CategoriaDTO> findAll();
    
    List<CategoriaDTO> findAllActivas();
    
    Optional<CategoriaDTO> findById(Long id);
    
    CategoriaDTO save(Categoria categoria);
    
    void deleteById(Long id);
    
    List<CategoriaDTO> findByBusqueda(String busqueda);
    
    boolean existsByNombre(String nombre);
    
    void reordenarCategorias(List<Long> idsOrdenados);
} 