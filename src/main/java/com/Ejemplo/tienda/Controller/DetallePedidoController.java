package com.Ejemplo.tienda.Controller;


import com.Ejemplo.tienda.Models.DetallePedido;
import com.Ejemplo.tienda.Service.interfaces.DetallePedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/detallepedidos")
public class DetallePedidoController {

    @Autowired
    private DetallePedidoService detallePedidoService;

    @GetMapping
    public List<DetallePedido> getAll() {
        return detallePedidoService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<DetallePedido> getById(@PathVariable Long id) {
        return detallePedidoService.findById(id);
    }

    @PostMapping
    public DetallePedido create(@RequestBody DetallePedido detallePedido) {
        return detallePedidoService.save(detallePedido);
    }

    @PutMapping("/{id}")
    public DetallePedido update(@PathVariable Long id, @RequestBody DetallePedido detallePedido) {
        detallePedido.setId(id);
        return detallePedidoService.save(detallePedido);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        detallePedidoService.deleteById(id);
    }
}
