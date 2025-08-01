package com.Ejemplo.tienda.Controller;

import com.Ejemplo.tienda.Dto.FacturadorItemDTO;
import com.Ejemplo.tienda.Models.Producto;
import com.Ejemplo.tienda.Repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/facturador")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class FacturadorController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/buscar")
    public ResponseEntity<List<FacturadorItemDTO>> buscarProductos(@RequestParam String busqueda) {
        try {
            List<Producto> productos = productoRepository.findByFacturadorBusqueda(busqueda);
            List<FacturadorItemDTO> items = productos.stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/producto/{codigoInterno}")
    public ResponseEntity<FacturadorItemDTO> buscarPorCodigo(@PathVariable String codigoInterno) {
        try {
            Optional<Producto> producto = productoRepository.findByCodigoInternoAndActivoTrue(codigoInterno);
            if (producto.isPresent()) {
                return ResponseEntity.ok(convertirADTO(producto.get()));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/productos-rapidos")
    public ResponseEntity<List<FacturadorItemDTO>> productosRapidos() {
        try {
            // Obtener los primeros 10 productos activos para acceso rápido
            List<Producto> productos = productoRepository.findByActivoTrueOrderByNombreAsc(null)
                    .getContent()
                    .stream()
                    .limit(10)
                    .collect(Collectors.toList());
            
            List<FacturadorItemDTO> items = productos.stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private FacturadorItemDTO convertirADTO(Producto producto) {
        FacturadorItemDTO dto = new FacturadorItemDTO();
        dto.setId(producto.getId());
        dto.setCodigoInterno(producto.getCodigoInterno());
        dto.setNombre(producto.getNombre());
        dto.setCategoria(producto.getCategoria());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setCantidad(1); // Cantidad por defecto
        dto.setSubtotal(producto.getPrecio());
        dto.setImagenUrl(producto.getImagenUrl());
        return dto;
    }
} 