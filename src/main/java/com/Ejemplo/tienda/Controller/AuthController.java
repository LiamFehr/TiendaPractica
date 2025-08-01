
package com.Ejemplo.tienda.Controller;

import com.Ejemplo.tienda.Dto.LoginRequest;
import com.Ejemplo.tienda.Dto.UsuarioRegistroDTO;
import com.Ejemplo.tienda.Models.Role;
import com.Ejemplo.tienda.Models.Usuario;
import com.Ejemplo.tienda.Security.JwtUtils;
import com.Ejemplo.tienda.Service.AuthService;
import com.Ejemplo.tienda.Service.impl.UsuarioDetailsServiceImpl;
import com.Ejemplo.tienda.Service.interfaces.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final UsuarioDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AuthService authService;

    public AuthController(UsuarioService usuarioService, 
                         UsuarioDetailsServiceImpl userDetailsService,
                         AuthenticationManager authenticationManager,
                         JwtUtils jwtUtils,
                         AuthService authService) {
        this.usuarioService = usuarioService;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UsuarioRegistroDTO dto) {
        try {
            Usuario usuario = authService.registrarUsuario(dto);
            usuario.setRole(Role.ROLE_USER);
            return ResponseEntity.status(201).body(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error en el registro: " + e.getMessage());
        }
    }

    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody UsuarioRegistroDTO dto) {
        try {
            Usuario usuario = authService.registrarUsuario(dto);
            usuario.setRole(Role.ROLE_ADMIN);
            return ResponseEntity.status(201).body(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error en el registro: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(req.getEmail());
            String token = jwtUtils.generateJwt(userDetails.getUsername());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (BadCredentialsException e) {
            throw e; // Dejar que el GlobalExceptionHandler maneje esto
        } catch (UsernameNotFoundException e) {
            throw e; // Dejar que el GlobalExceptionHandler maneje esto
        }
    }
}
