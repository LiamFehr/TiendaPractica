package com.Ejemplo.tienda.Service.impl;
 

import com.Ejemplo.tienda.Models.DetallePedido;
import com.Ejemplo.tienda.Repository.DetallePedidoRepository;
import com.Ejemplo.tienda.Service.interfaces.DetallePedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetallePedidosServiceImpl implements DetallePedidoService {

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Override
    public List<DetallePedido> findAll() {
        return detallePedidoRepository.findAll();
    }

    @Override
    public Optional<DetallePedido> findById(Long id) {
        return detallePedidoRepository.findById(id);
    }

    @Override
    public DetallePedido save(DetallePedido detallePedido) {
        return detallePedidoRepository.save(detallePedido);
    }

    @Override
    public void deleteById(Long id) {
        detallePedidoRepository.deleteById(id);
    }
}
