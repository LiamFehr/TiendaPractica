package com.Ejemplo.tienda.Controller;

import com.Ejemplo.tienda.Dto.PagoRequestDTO;
import com.Ejemplo.tienda.Dto.PagoResponseDTO;
import com.Ejemplo.tienda.Service.interfaces.MercadoPagoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private MercadoPagoService mercadoPagoService;

    @PostMapping("/crear")
    public ResponseEntity<PagoResponseDTO> crearPago(@Valid @RequestBody PagoRequestDTO request) {
        try {
            PagoResponseDTO pago = mercadoPagoService.crearPago(request);
            pago.setMensaje("Pago creado exitosamente");
            return ResponseEntity.ok(pago);
        } catch (Exception e) {
            PagoResponseDTO errorPago = new PagoResponseDTO();
            errorPago.setMensaje("Error al crear pago: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorPago);
        }
    }

    @GetMapping("/{mercadopagoId}")
    public ResponseEntity<PagoResponseDTO> obtenerPago(@PathVariable String mercadopagoId) {
        try {
            PagoResponseDTO pago = mercadoPagoService.obtenerPago(mercadopagoId);
            return ResponseEntity.ok(pago);
        } catch (Exception e) {
            PagoResponseDTO errorPago = new PagoResponseDTO();
            errorPago.setMensaje("Error al obtener pago: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorPago);
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestParam String mercadopagoId,
            @RequestParam String estado) {
        try {
            mercadoPagoService.procesarWebhook(mercadopagoId, estado);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 