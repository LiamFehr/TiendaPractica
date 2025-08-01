package com.Ejemplo.tienda.Service.interfaces;

import com.Ejemplo.tienda.Dto.CarritoDTO;
import com.Ejemplo.tienda.Dto.CarritoItemDTO;

public interface CarritoService {
    
    CarritoDTO obtenerCarrito(Long usuarioId);
    
    CarritoDTO agregarProducto(Long usuarioId, Long productoId, Integer cantidad);
    
    CarritoDTO actualizarCantidad(Long usuarioId, Long productoId, Integer cantidad);
    
    CarritoDTO eliminarProducto(Long usuarioId, Long productoId);
    
    void limpiarCarrito(Long usuarioId);
    
    CarritoItemDTO obtenerItem(Long usuarioId, Long productoId);
} 