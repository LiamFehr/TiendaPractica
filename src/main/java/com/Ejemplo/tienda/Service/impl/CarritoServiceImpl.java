package com.Ejemplo.tienda.Service.impl;

import com.Ejemplo.tienda.Dto.CarritoDTO;
import com.Ejemplo.tienda.Dto.CarritoItemDTO;
import com.Ejemplo.tienda.Models.CarritoItem;
import com.Ejemplo.tienda.Models.Producto;
import com.Ejemplo.tienda.Models.Usuario;
import com.Ejemplo.tienda.Repository.CarritoItemRepository;
import com.Ejemplo.tienda.Repository.ProductoRepository;
import com.Ejemplo.tienda.Repository.UsuarioRepository;
import com.Ejemplo.tienda.Service.interfaces.CarritoService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarritoServiceImpl implements CarritoService {

    private final CarritoItemRepository carritoItemRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public CarritoServiceImpl(CarritoItemRepository carritoItemRepository,
                             ProductoRepository productoRepository,
                             UsuarioRepository usuarioRepository) {
        this.carritoItemRepository = carritoItemRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public CarritoDTO obtenerCarrito(Long usuarioId) {
        List<CarritoItem> items = carritoItemRepository.findActiveItemsByUsuario(usuarioId);
        
        CarritoDTO carrito = new CarritoDTO();
        carrito.setItems(items.stream().map(this::convertirADTO).collect(Collectors.toList()));
        
        // Calcular totales
        int totalItems = items.stream().mapToInt(CarritoItem::getCantidad).sum();
        double subtotal = items.stream().mapToDouble(item -> 
            item.getCantidad() * item.getProducto().getPrecio()).sum();
        
        carrito.setTotalItems(totalItems);
        carrito.setSubtotal(subtotal);
        carrito.setTotal(subtotal); // Por ahora sin impuestos
        
        return carrito;
    }

    @Override
    public CarritoDTO agregarProducto(Long usuarioId, Long productoId, Integer cantidad) {
        // Verificar que el producto existe y está activo
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (!productoOpt.isPresent() || !productoOpt.get().getActivo()) {
            throw new RuntimeException("Producto no encontrado o no disponible");
        }
        
        Producto producto = productoOpt.get();
        
        // Verificar stock
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente");
        }
        
        // Verificar si ya existe en el carrito
        Optional<CarritoItem> itemExistente = carritoItemRepository.findByUsuarioIdAndProductoId(usuarioId, productoId);
        
        if (itemExistente.isPresent()) {
            // Actualizar cantidad
            CarritoItem item = itemExistente.get();
            item.setCantidad(item.getCantidad() + cantidad);
            carritoItemRepository.save(item);
        } else {
            // Crear nuevo item
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            if (!usuarioOpt.isPresent()) {
                throw new RuntimeException("Usuario no encontrado");
            }
            
            CarritoItem nuevoItem = new CarritoItem();
            nuevoItem.setUsuario(usuarioOpt.get());
            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(cantidad);
            carritoItemRepository.save(nuevoItem);
        }
        
        return obtenerCarrito(usuarioId);
    }

    @Override
    public CarritoDTO actualizarCantidad(Long usuarioId, Long productoId, Integer cantidad) {
        Optional<CarritoItem> itemOpt = carritoItemRepository.findByUsuarioIdAndProductoId(usuarioId, productoId);
        
        if (!itemOpt.isPresent()) {
            throw new RuntimeException("Producto no encontrado en el carrito");
        }
        
        CarritoItem item = itemOpt.get();
        
        // Verificar stock
        if (item.getProducto().getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente");
        }
        
        if (cantidad <= 0) {
            carritoItemRepository.delete(item);
        } else {
            item.setCantidad(cantidad);
            carritoItemRepository.save(item);
        }
        
        return obtenerCarrito(usuarioId);
    }

    @Override
    public CarritoDTO eliminarProducto(Long usuarioId, Long productoId) {
        carritoItemRepository.deleteByUsuarioIdAndProductoId(usuarioId, productoId);
        return obtenerCarrito(usuarioId);
    }

    @Override
    public void limpiarCarrito(Long usuarioId) {
        carritoItemRepository.deleteByUsuarioId(usuarioId);
    }

    @Override
    public CarritoItemDTO obtenerItem(Long usuarioId, Long productoId) {
        Optional<CarritoItem> itemOpt = carritoItemRepository.findByUsuarioIdAndProductoId(usuarioId, productoId);
        return itemOpt.map(this::convertirADTO).orElse(null);
    }

    private CarritoItemDTO convertirADTO(CarritoItem item) {
        CarritoItemDTO dto = new CarritoItemDTO();
        dto.setId(item.getId());
        dto.setProductoId(item.getProducto().getId());
        dto.setNombreProducto(item.getProducto().getNombre());
        dto.setImagenUrl(item.getProducto().getImagenUrl());
        dto.setPrecio(item.getProducto().getPrecio());
        dto.setCantidad(item.getCantidad());
        dto.setSubtotal(item.getCantidad() * item.getProducto().getPrecio());
        dto.setFechaAgregado(item.getFechaAgregado());
        return dto;
    }
} 