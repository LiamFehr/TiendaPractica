package com.Ejemplo.tienda.Controller;

import com.Ejemplo.tienda.Dto.CategoriaCrearDTO;
import com.Ejemplo.tienda.Dto.CategoriaDTO;
import com.Ejemplo.tienda.Models.Categoria;
import com.Ejemplo.tienda.Service.interfaces.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // Convertir CategoriaCrearDTO a Categoria
    private Categoria convertirACategoria(CategoriaCrearDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        categoria.setColor(dto.getColor());
        categoria.setOrden(dto.getOrden());
        return categoria;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> getAll() {
        List<CategoriaDTO> categorias = categoriaService.findAllActivas();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<CategoriaDTO>> getAllAdmin() {
        List<CategoriaDTO> categorias = categoriaService.findAll();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> getById(@PathVariable Long id) {
        Optional<CategoriaDTO> categoria = categoriaService.findById(id);
        if (categoria.isPresent()) {
            return ResponseEntity.ok(categoria.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CategoriaDTO> create(@Valid @RequestBody CategoriaCrearDTO dto) {
        try {
            // Verificar que no existe una categoría con el mismo nombre
            if (categoriaService.existsByNombre(dto.getNombre())) {
                return ResponseEntity.badRequest().build();
            }

            Categoria categoria = convertirACategoria(dto);
            CategoriaDTO saved = categoriaService.save(categoria);
            return ResponseEntity.status(201).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CategoriaDTO> update(@PathVariable Long id, @Valid @RequestBody CategoriaCrearDTO dto) {
        Optional<CategoriaDTO> categoriaOpt = categoriaService.findById(id);
        if (!categoriaOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Categoria categoria = convertirACategoria(dto);
            categoria.setId(id);
            CategoriaDTO updated = categoriaService.save(categoria);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<CategoriaDTO> categoria = categoriaService.findById(id);
        if (!categoria.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        categoriaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<CategoriaDTO>> search(@RequestParam String busqueda) {
        List<CategoriaDTO> categorias = categoriaService.findByBusqueda(busqueda);
        return ResponseEntity.ok(categorias);
    }

    @PostMapping("/reordenar")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> reordenar(@RequestBody List<Long> idsOrdenados) {
        try {
            categoriaService.reordenarCategorias(idsOrdenados);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 