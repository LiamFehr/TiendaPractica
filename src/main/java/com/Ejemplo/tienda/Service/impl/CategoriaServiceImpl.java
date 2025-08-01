package com.Ejemplo.tienda.Service.impl;

import com.Ejemplo.tienda.Dto.CategoriaDTO;
import com.Ejemplo.tienda.Models.Categoria;
import com.Ejemplo.tienda.Repository.CategoriaRepository;
import com.Ejemplo.tienda.Service.interfaces.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public List<CategoriaDTO> findAll() {
        return categoriaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoriaDTO> findAllActivas() {
        return categoriaRepository.findByActivoTrueOrderByOrdenAsc().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CategoriaDTO> findById(Long id) {
        return categoriaRepository.findById(id)
                .map(this::convertirADTO);
    }

    @Override
    public CategoriaDTO save(Categoria categoria) {
        // Si es nueva categoría y no tiene orden, asignar el siguiente
        if (categoria.getId() == null && categoria.getOrden() == null) {
            List<Categoria> categorias = categoriaRepository.findByActivoTrueOrderByOrdenAsc();
            int siguienteOrden = categorias.isEmpty() ? 1 : categorias.get(categorias.size() - 1).getOrden() + 1;
            categoria.setOrden(siguienteOrden);
        }
        
        Categoria saved = categoriaRepository.save(categoria);
        return convertirADTO(saved);
    }

    @Override
    public void deleteById(Long id) {
        categoriaRepository.deleteById(id);
    }

    @Override
    public List<CategoriaDTO> findByBusqueda(String busqueda) {
        return categoriaRepository.findByBusqueda(busqueda).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return categoriaRepository.existsByNombre(nombre);
    }

    @Override
    public void reordenarCategorias(List<Long> idsOrdenados) {
        for (int i = 0; i < idsOrdenados.size(); i++) {
            Optional<Categoria> categoria = categoriaRepository.findById(idsOrdenados.get(i));
            if (categoria.isPresent()) {
                Categoria cat = categoria.get();
                cat.setOrden(i + 1);
                categoriaRepository.save(cat);
            }
        }
    }

    private CategoriaDTO convertirADTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        dto.setColor(categoria.getColor());
        dto.setOrden(categoria.getOrden());
        dto.setActivo(categoria.isActivo());
        dto.setFechaCreacion(categoria.getFechaCreacion());
        dto.setFechaActualizacion(categoria.getFechaActualizacion());
        
        // Contar productos en esta categoría
        Long totalProductos = categoriaRepository.countProductosByCategoria(categoria.getId());
        dto.setTotalProductos(totalProductos != null ? totalProductos.intValue() : 0);
        
        return dto;
    }
} 