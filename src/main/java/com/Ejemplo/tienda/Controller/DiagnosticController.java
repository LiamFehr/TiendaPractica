package com.Ejemplo.tienda.Controller;

import com.Ejemplo.tienda.Security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/diagnostic")
public class DiagnosticController {

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/auth-status")
    public ResponseEntity<Map<String, Object>> getAuthStatus() {
        Map<String, Object> response = new HashMap<>();
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            response.put("authenticated", true);
            response.put("username", auth.getName());
            response.put("authorities", auth.getAuthorities().stream()
                    .map(Object::toString)
                    .collect(Collectors.toList()));
            
            if (auth.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                response.put("userDetails", Map.of(
                    "username", userDetails.getUsername(),
                    "authorities", userDetails.getAuthorities().stream()
                            .map(Object::toString)
                            .collect(Collectors.toList()),
                    "enabled", userDetails.isEnabled(),
                    "accountNonExpired", userDetails.isAccountNonExpired(),
                    "credentialsNonExpired", userDetails.isCredentialsNonExpired(),
                    "accountNonLocked", userDetails.isAccountNonLocked()
                ));
            }
        } else {
            response.put("authenticated", false);
            response.put("message", "No autenticado o usuario anónimo");
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-admin")
    public ResponseEntity<Map<String, String>> testAdminAccess() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Acceso de administrador confirmado");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-user")
    public ResponseEntity<Map<String, String>> testUserAccess() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Acceso de usuario confirmado");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-token")
    public ResponseEntity<Map<String, Object>> testToken() {
        Map<String, Object> response = new HashMap<>();
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        response.put("authentication", auth != null ? auth.getName() : "null");
        response.put("authenticated", auth != null && auth.isAuthenticated());
        response.put("isAnonymous", auth != null && "anonymousUser".equals(auth.getName()));
        
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            response.put("userDetails", Map.of(
                "username", userDetails.getUsername(),
                "authorities", userDetails.getAuthorities().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList())
            ));
        }
        
        return ResponseEntity.ok(response);
    }
} 