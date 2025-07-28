package com.Ejemplo.tienda.Service.interfaces;

import com.Ejemplo.tienda.Models.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<Usuario> findAll();
    Optional<Usuario> findById(Long id);
    Usuario save(Usuario usuario);
    void deleteById(Long id);
    Optional<Usuario> findByEmail(String email); // ✅ corrección acá
}
