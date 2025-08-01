// src/main/java/com/Ejemplo/tienda/Models/Usuario.java
package com.Ejemplo.tienda.Models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nuevo campo para el nombre completo
    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Si prefieres no usar Lombok, elimina las anotaciones y añade manualmente:
    //
    // public String getNombre() { return nombre; }
    // public void setNombre(String nombre) { this.nombre = nombre; }
    // (y lo mismo para los demás campos)
}
