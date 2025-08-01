package com.Ejemplo.tienda.Controller;

import com.Ejemplo.tienda.Dto.CarritoDTO;
import com.Ejemplo.tienda.Service.interfaces.CarritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Aquí deberías obtener el ID del usuario desde el token JWT
        // Por ahora, asumimos que el email es único y lo usamos como identificador
        String email = auth.getName();
        // En una implementación real, deberías buscar el usuario por email y obtener su ID
        // Por simplicidad, usamos 1 como ID por defecto
        return 1L; // TODO: Implementar obtención real del ID del usuario
    }

    @GetMapping
    public ResponseEntity<CarritoDTO> obtenerCarrito() {
        try {
            Long usuarioId = getCurrentUserId();
            CarritoDTO carrito = carritoService.obtenerCarrito(usuarioId);
            return ResponseEntity.ok(carrito);
        } catch (Exception e) {
            CarritoDTO errorCarrito = new CarritoDTO();
            errorCarrito.setMensaje("Error al obtener el carrito: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorCarrito);
        }
    }

    @PostMapping("/agregar")
    public ResponseEntity<CarritoDTO> agregarProducto(
            @RequestParam Long productoId,
            @RequestParam(defaultValue = "1") Integer cantidad) {
        try {
            Long usuarioId = getCurrentUserId();
            CarritoDTO carrito = carritoService.agregarProducto(usuarioId, productoId, cantidad);
            carrito.setMensaje("Producto agregado al carrito");
            return ResponseEntity.ok(carrito);
        } catch (Exception e) {
            CarritoDTO errorCarrito = new CarritoDTO();
            errorCarrito.setMensaje("Error al agregar producto: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorCarrito);
        }
    }

    @PutMapping("/actualizar-cantidad")
    public ResponseEntity<CarritoDTO> actualizarCantidad(
            @RequestParam Long productoId,
            @RequestParam Integer cantidad) {
        try {
            Long usuarioId = getCurrentUserId();
            CarritoDTO carrito = carritoService.actualizarCantidad(usuarioId, productoId, cantidad);
            carrito.setMensaje("Cantidad actualizada");
            return ResponseEntity.ok(carrito);
        } catch (Exception e) {
            CarritoDTO errorCarrito = new CarritoDTO();
            errorCarrito.setMensaje("Error al actualizar cantidad: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorCarrito);
        }
    }

    @DeleteMapping("/eliminar/{productoId}")
    public ResponseEntity<CarritoDTO> eliminarProducto(@PathVariable Long productoId) {
        try {
            Long usuarioId = getCurrentUserId();
            CarritoDTO carrito = carritoService.eliminarProducto(usuarioId, productoId);
            carrito.setMensaje("Producto eliminado del carrito");
            return ResponseEntity.ok(carrito);
        } catch (Exception e) {
            CarritoDTO errorCarrito = new CarritoDTO();
            errorCarrito.setMensaje("Error al eliminar producto: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorCarrito);
        }
    }

    @DeleteMapping("/limpiar")
    public ResponseEntity<Void> limpiarCarrito() {
        try {
            Long usuarioId = getCurrentUserId();
            carritoService.limpiarCarrito(usuarioId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 