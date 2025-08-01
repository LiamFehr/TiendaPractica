package com.Ejemplo.tienda.Controller;

import com.Ejemplo.tienda.Models.Usuario;
import com.Ejemplo.tienda.Service.interfaces.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<Usuario> getAll() {
        return usuarioService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Usuario> getById(@PathVariable Long id) {
        return usuarioService.findById(id);
    }

    @PostMapping
    public Usuario create(@RequestBody Usuario usuario) {
        return usuarioService.save(usuario);
    }

    @PutMapping("/{id}")
    public Usuario update(@PathVariable Long id, @RequestBody Usuario usuario) {
        // Obtener el usuario existente
        Optional<Usuario> existingUser = usuarioService.findById(id);
        if (existingUser.isPresent()) {
            Usuario existing = existingUser.get();
            
            // Actualizar campos
            existing.setNombre(usuario.getNombre());
            existing.setEmail(usuario.getEmail());
            existing.setRole(usuario.getRole());
            
            // Solo actualizar password si se proporciona uno nuevo
            if (usuario.getPassword() != null && !usuario.getPassword().trim().isEmpty()) {
                existing.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }
            
            return usuarioService.save(existing);
        }
        throw new RuntimeException("Usuario no encontrado con ID: " + id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        usuarioService.deleteById(id);
    }
}
