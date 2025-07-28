package com.Ejemplo.tienda.Service.interfaces;

import com.Ejemplo.tienda.Models.Pedido;
import java.util.List;
import java.util.Optional;
public interface PedidoService 
{
	List<Pedido> findAll();
    Optional<Pedido> findById(Long id);
    Pedido save(Pedido pedido);
    void deleteById(Long id);
}
