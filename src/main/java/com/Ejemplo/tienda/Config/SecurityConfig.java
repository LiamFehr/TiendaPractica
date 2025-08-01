// src/main/java/com/Ejemplo/tienda/Config/SecurityConfig.java
package com.Ejemplo.tienda.Config;

import com.Ejemplo.tienda.Security.JwtFilter;
import com.Ejemplo.tienda.Service.impl.UsuarioDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UsuarioDetailsServiceImpl userDetailsService;

    public SecurityConfig(JwtFilter jwtFilter, UsuarioDetailsServiceImpl userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .csrf(AbstractHttpConfigurer::disable)
          .cors(Customizer.withDefaults())
          .authorizeHttpRequests(auth -> auth
              // Permitir acceso a archivos estáticos
              .requestMatchers("/", "/index.html", "/admin.html").permitAll()
              .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
              .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
              .requestMatchers("/api/auth/register-admin").authenticated()
              .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
              .requestMatchers("/api/productos/filtrar").permitAll() // Permitir filtros sin autenticación
              .requestMatchers("/api/productos/destacados").permitAll() // Productos destacados públicos
              .requestMatchers("/api/productos/categoria-simple/**").permitAll() // Búsqueda por categoría pública
              .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
              .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
              .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
              .requestMatchers(HttpMethod.GET, "/api/categorias").permitAll() // Categorías públicas
              .requestMatchers("/api/categorias/buscar").permitAll() // Búsqueda de categorías pública
              .requestMatchers("/api/categorias/**").hasRole("ADMIN") // Resto de categorías solo admin
              .requestMatchers("/api/carrito/**").authenticated() // Carrito requiere autenticación
              .requestMatchers("/api/pagos/**").authenticated() // Pagos requiere autenticación
              .requestMatchers("/api/facturador/**").hasRole("ADMIN") // Facturador solo admin
              .requestMatchers("/api/pedidos/**").authenticated() // Pedidos requiere autenticación
              .requestMatchers("/api/usuarios/**").hasRole("ADMIN") // Usuarios solo admin
              .anyRequest().authenticated()
          )
          .sessionManagement(sm -> sm
              .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          )
          .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}
