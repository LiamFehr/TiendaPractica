package com.Ejemplo.tienda.Controller;

import com.Ejemplo.tienda.Dto.ProductoCrearDTO;
import com.Ejemplo.tienda.Dto.ProductoDTO;
import com.Ejemplo.tienda.Dto.ProductoFiltroDTO;
import com.Ejemplo.tienda.Dto.ProductoPaginadoDTO;
import com.Ejemplo.tienda.Models.Producto;
import com.Ejemplo.tienda.Service.interfaces.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // Convertir Producto a ProductoDTO
    private ProductoDTO convertirADTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setImagenUrl(producto.getImagenUrl());
        dto.setCategoria(producto.getCategoria());
        dto.setCodigoInterno(producto.getCodigoInterno());
        dto.setFechaCreacion(producto.getFechaCreacion());
        dto.setActivo(producto.getActivo());
        return dto;
    }

    // Convertir ProductoCrearDTO a Producto
    private Producto convertirAProducto(ProductoCrearDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setImagenUrl(dto.getImagenUrl());
        producto.setCategoria(dto.getCategoria());
        
        // Usar código interno proporcionado o generar uno automáticamente
        if (dto.getCodigoInterno() != null && !dto.getCodigoInterno().trim().isEmpty()) {
            producto.setCodigoInterno(dto.getCodigoInterno().toUpperCase());
        } else {
            producto.setCodigoInterno(generarCodigoInterno(dto.getNombre()));
        }
        
        producto.setActivo(true); // Asegurar que el producto esté activo al crearlo
        return producto;
    }
    
    // Generar código interno único
    private String generarCodigoInterno(String nombre) {
        try {
            String codigo = nombre.toUpperCase()
                    .replaceAll("[^A-Z0-9]", "")
                    .substring(0, Math.min(6, nombre.length()));
            return codigo + "-" + System.currentTimeMillis() % 10000;
        } catch (Exception e) {
            // Fallback si hay algún problema con el nombre
            return "PROD-" + System.currentTimeMillis() % 10000;
        }
    }

    // Endpoints básicos (mantener compatibilidad)
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> getAll() {
        List<ProductoDTO> productos = productoService.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getById(@PathVariable Long id) {
        Optional<Producto> producto = productoService.findById(id);
        if (producto.isPresent()) {
            return ResponseEntity.ok(convertirADTO(producto.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductoDTO> create(@Valid @RequestBody ProductoCrearDTO dto) {
        try {
            Producto producto = convertirAProducto(dto);
            Producto saved = productoService.save(producto);
            return ResponseEntity.status(201).body(convertirADTO(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductoDTO> update(@PathVariable Long id, @Valid @RequestBody ProductoCrearDTO dto) {
        if (!productoService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            Producto producto = convertirAProducto(dto);
            producto.setId(id);
            Producto updated = productoService.save(producto);
            return ResponseEntity.ok(convertirADTO(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!productoService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Nuevos endpoints paginados
    @GetMapping("/paginado")
    public ResponseEntity<ProductoPaginadoDTO> getAllPaginado(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio,
            @RequestParam(defaultValue = "id") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion) {
        
        ProductoPaginadoDTO resultado = productoService.findAllPaginado(pagina, tamanio, ordenarPor, direccion);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/categoria/{categoria}/paginado")
    public ResponseEntity<ProductoPaginadoDTO> getByCategoriaPaginado(
            @PathVariable String categoria,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        
        ProductoPaginadoDTO resultado = productoService.findByCategoriaPaginado(categoria, pagina, tamanio);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/buscar/paginado")
    public ResponseEntity<ProductoPaginadoDTO> searchByNombrePaginado(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        
        ProductoPaginadoDTO resultado = productoService.findByNombrePaginado(nombre, pagina, tamanio);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/precio/paginado")
    public ResponseEntity<ProductoPaginadoDTO> getByPrecioRangePaginado(
            @RequestParam Double precioMin, 
            @RequestParam Double precioMax,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        
        ProductoPaginadoDTO resultado = productoService.findByPrecioBetweenPaginado(precioMin, precioMax, pagina, tamanio);
        return ResponseEntity.ok(resultado);
    }

    // Filtros avanzados
    @PostMapping("/filtrar")
    public ResponseEntity<ProductoPaginadoDTO> filtrarProductos(@RequestBody ProductoFiltroDTO filtros) {
        ProductoPaginadoDTO resultado = productoService.findByFiltrosAvanzados(filtros);
        return ResponseEntity.ok(resultado);
    }

    // Productos destacados
    @GetMapping("/destacados")
    public ResponseEntity<ProductoPaginadoDTO> getProductosDestacados(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        
        ProductoPaginadoDTO resultado = productoService.findProductosDestacados(pagina, tamanio);
        return ResponseEntity.ok(resultado);
    }

    // Endpoints de estadísticas
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> getEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("precioPromedio", productoService.getPrecioPromedio());
        estadisticas.put("precioMinimo", productoService.getPrecioMinimo());
        estadisticas.put("precioMaximo", productoService.getPrecioMaximo());
        estadisticas.put("totalProductos", productoService.findAll().size());
        estadisticas.put("categorias", productoService.findAllCategorias());
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/estadisticas/categoria/{categoria}")
    public ResponseEntity<Map<String, Object>> getEstadisticasCategoria(@PathVariable String categoria) {
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("categoria", categoria);
        estadisticas.put("totalProductos", productoService.countByCategoria(categoria));
        return ResponseEntity.ok(estadisticas);
    }

    // Endpoints de búsqueda (mantener compatibilidad)
    @GetMapping("/buscar-simple")
    public ResponseEntity<List<ProductoDTO>> searchByNombre(@RequestParam String nombre) {
        List<ProductoDTO> productos = productoService.findByNombre(nombre)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/precio-simple")
    public ResponseEntity<List<ProductoDTO>> getByPrecioRange(
            @RequestParam Double precioMin, 
            @RequestParam Double precioMax) {
        List<ProductoDTO> productos = productoService.findByPrecioBetween(precioMin, precioMax)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/stock-bajo")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ProductoDTO>> getStockBajo(@RequestParam(defaultValue = "5") Integer limite) {
        List<ProductoDTO> productos = productoService.findByStockBajo(limite)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> getAllCategorias() {
        List<String> categorias = productoService.findAllCategorias();
        return ResponseEntity.ok(categorias);
    }

    // Endpoint simple para buscar por categoría (sin paginación)
    @GetMapping("/categoria-simple/{categoria}")
    public ResponseEntity<List<ProductoDTO>> getByCategoriaSimple(@PathVariable String categoria) {
        List<ProductoDTO> productos = productoService.findByCategoria(categoria)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productos);
    }
}
