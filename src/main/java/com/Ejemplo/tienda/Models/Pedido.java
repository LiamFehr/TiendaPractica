package com.Ejemplo.tienda.Models;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Pedido 
{
	@Id
	@GeneratedValue 
	private long id;
	private LocalDate fecha;
	private Double total;
	private String estado;
	
	@ManyToOne
	 @JoinColumn(name = "usuario_id")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnoreProperties("pedidos")
	private Usuario usuario;
	
	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
	@EqualsAndHashCode.Exclude
	@JsonIgnoreProperties("pedidos")
	private List<DetallePedido>detalles;
}
