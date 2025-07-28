package com.Ejemplo.tienda.Models;

import java.util.List;

import jakarta.persistence.Id;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios")
@Data
@Entity
public class Usuario 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String nombre;
	@Column(unique = true )
	private String email;
	private String password;
	private String rol;

	  @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
	  @JsonIgnoreProperties("usuario")
	  private List<Pedido> pedidos;

}
