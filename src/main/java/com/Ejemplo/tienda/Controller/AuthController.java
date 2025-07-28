package com.Ejemplo.tienda.Controller;

import com.Ejemplo.tienda.Dto.AuthResponse;
import com.Ejemplo.tienda.Dto.LoginRequest;
import com.Ejemplo.tienda.Security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authManager;
    @Autowired private com.Ejemplo.tienda.Service.impl.UsuarioDetailsServiceImpl uds;
    @Autowired private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody com.Ejemplo.tienda.Models.Usuario u) {
        // tu lógica de registro...
        return ResponseEntity.ok("Usuario registrado");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        UserDetails udsObj = uds.loadUserByUsername(req.getEmail());
        String token = jwtUtils.generateJwt(udsObj.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}

