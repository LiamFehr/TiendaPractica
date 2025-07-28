package com.Ejemplo.tienda.Dto;

import lombok.Data;

@Data
public class UsuarioRegistroDTO {
    private String nombre;
    private String email;
    private String password;
}