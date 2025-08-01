package com.Ejemplo.tienda.Service.impl;

import com.Ejemplo.tienda.Dto.ProductoFiltroDTO;
import com.Ejemplo.tienda.Dto.ProductoPaginadoDTO;
import com.Ejemplo.tienda.Models.Producto;
import com.Ejemplo.tienda.Repository.ProductoRepository;
import com.Ejemplo.tienda.Service.interfaces.ProductoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Producto> findAll() {
        return productoRepository.findByActivoTrue(Pageable.unpaged()).getContent();
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id)
                .filter(Producto::getActivo);
    }

    @Override
    public Producto save(Producto producto) {
        if (producto.getId() == null) {
            producto.setActivo(true);
        }
        return productoRepository.save(producto);
    }

    @Override
    public void deleteById(Long id) {
        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isPresent()) {
            Producto p = producto.get();
            p.setActivo(false);
            productoRepository.save(p);
        }
    }

    @Override
    public boolean existsById(Long id) {
        return productoRepository.findById(id)
                .map(Producto::getActivo)
                .orElse(false);
    }

    // Métodos paginados
    @Override
    public ProductoPaginadoDTO findAllPaginado(int pagina, int tamanio, String ordenarPor, String direccion) {
        Sort sort = crearSort(ordenarPor, direccion);
        Pageable pageable = PageRequest.of(pagina, tamanio, sort);
        Page<Producto> page = productoRepository.findByActivoTrue(pageable);
        return convertirAPaginadoDTO(page);
    }

    @Override
    public ProductoPaginadoDTO findByCategoriaPaginado(String categoria, int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio);
        Page<Producto> page = productoRepository.findByCategoriaAndActivoTrue(categoria, pageable);
        return convertirAPaginadoDTO(page);
    }

    @Override
    public ProductoPaginadoDTO findByNombrePaginado(String nombre, int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio);
        Page<Producto> page = productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre, pageable);
        return convertirAPaginadoDTO(page);
    }

    @Override
    public ProductoPaginadoDTO findByPrecioBetweenPaginado(Double precioMin, Double precioMax, int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio);
        Page<Producto> page = productoRepository.findByPrecioBetween(precioMin, precioMax, pageable);
        return convertirAPaginadoDTO(page);
    }

    @Override
    public ProductoPaginadoDTO findByFiltrosAvanzados(ProductoFiltroDTO filtros) {
        Sort sort = crearSort(filtros.getOrdenarPor(), filtros.getDireccion());
        Pageable pageable = PageRequest.of(filtros.getPagina(), filtros.getTamanio(), sort);
        
        Page<Producto> page = productoRepository.findByFiltrosAvanzados(
            filtros.getCategoria(),
            filtros.getPrecioMin(),
            filtros.getPrecioMax(),
            filtros.getNombre(),
            filtros.getStockMin(),
            pageable
        );
        
        return convertirAPaginadoDTO(page);
    }

    @Override
    public ProductoPaginadoDTO findProductosDestacados(int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio);
        Page<Producto> page = productoRepository.findProductosDestacados(pageable);
        return convertirAPaginadoDTO(page);
    }

    // Métodos de búsqueda (sin paginación para compatibilidad)
    @Override
    public List<Producto> findByCategoria(String categoria) {
        return productoRepository.findByCategoriaAndActivoTrue(categoria, Pageable.unpaged()).getContent();
    }

    @Override
    public List<Producto> findByNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre, Pageable.unpaged()).getContent();
    }

    @Override
    public List<Producto> findByPrecioBetween(Double precioMin, Double precioMax) {
        return productoRepository.findByPrecioBetween(precioMin, precioMax, Pageable.unpaged()).getContent();
    }

    @Override
    public List<Producto> findByStockBajo(Integer stockLimite) {
        return productoRepository.findByStockBajo(stockLimite);
    }

    @Override
    public List<String> findAllCategorias() {
        return productoRepository.findAllCategorias();
    }

    // Métodos de estadísticas
    @Override
    public long countByCategoria(String categoria) {
        return productoRepository.findByCategoriaAndActivoTrue(categoria, Pageable.unpaged()).getTotalElements();
    }

    @Override
    public double getPrecioPromedio() {
        List<Producto> productos = findAll();
        if (productos.isEmpty()) return 0.0;
        return productos.stream()
                .mapToDouble(Producto::getPrecio)
                .average()
                .orElse(0.0);
    }

    @Override
    public double getPrecioMinimo() {
        List<Producto> productos = findAll();
        if (productos.isEmpty()) return 0.0;
        return productos.stream()
                .mapToDouble(Producto::getPrecio)
                .min()
                .orElse(0.0);
    }

    @Override
    public double getPrecioMaximo() {
        List<Producto> productos = findAll();
        if (productos.isEmpty()) return 0.0;
        return productos.stream()
                .mapToDouble(Producto::getPrecio)
                .max()
                .orElse(0.0);
    }

    // Métodos auxiliares
    private Sort crearSort(String ordenarPor, String direccion) {
        if (ordenarPor == null || ordenarPor.isEmpty()) {
            return Sort.by(Sort.Direction.ASC, "id");
        }
        
        Sort.Direction direction = "desc".equalsIgnoreCase(direccion) ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        
        return Sort.by(direction, ordenarPor);
    }

    private ProductoPaginadoDTO convertirAPaginadoDTO(Page<Producto> page) {
        ProductoPaginadoDTO dto = new ProductoPaginadoDTO();
        dto.setProductos(page.getContent().stream()
                .map(this::convertirAProductoDTO)
                .collect(Collectors.toList()));
        dto.setPaginaActual(page.getNumber());
        dto.setTotalPaginas(page.getTotalPages());
        dto.setTotalElementos(page.getTotalElements());
        dto.setTamanioPagina(page.getSize());
        dto.setPrimeraPagina(page.isFirst());
        dto.setUltimaPagina(page.isLast());
        dto.setTieneSiguiente(page.hasNext());
        dto.setTieneAnterior(page.hasPrevious());
        return dto;
    }

    private com.Ejemplo.tienda.Dto.ProductoDTO convertirAProductoDTO(Producto producto) {
        com.Ejemplo.tienda.Dto.ProductoDTO dto = new com.Ejemplo.tienda.Dto.ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setImagenUrl(producto.getImagenUrl());
        dto.setCategoria(producto.getCategoria());
        dto.setFechaCreacion(producto.getFechaCreacion());
        dto.setActivo(producto.getActivo());
        return dto;
    }
}


