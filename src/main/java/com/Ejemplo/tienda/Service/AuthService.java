package com.Ejemplo.tienda.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Ejemplo.tienda.Dto.UsuarioRegistroDTO;
import com.Ejemplo.tienda.Models.Usuario;
import com.Ejemplo.tienda.Repository.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import com.Ejemplo.tienda.Models.Role;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario registrarUsuario(UsuarioRegistroDTO dto) {
        try {
            // Verificar si el usuario ya existe
            if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new RuntimeException("El email ya está registrado");
            }
            
            Usuario usuario = new Usuario();
            usuario.setNombre(dto.getNombre());
            usuario.setEmail(dto.getEmail());
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
            usuario.setRole(Role.ROLE_USER);
            return usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Error de integridad de datos: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage());
        }
    }
}
