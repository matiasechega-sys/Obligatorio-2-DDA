package com.example.streaming.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Contenido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código es obligatorio")
    @Column(unique = true)
    private String codigo;

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @Min(value = 1, message = "La duración debe ser mayor a 0")
    private int duracion;

    @Min(value = 1900, message = "El año debe ser válido")
    private int anio;

    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private double precio;

    private boolean exclusivoPremium;

    public Contenido() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getDuracion() { return duracion; }
    public void setDuracion(int duracion) { this.duracion = duracion; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public boolean isExclusivoPremium() { return exclusivoPremium; }
    public void setExclusivoPremium(boolean exclusivoPremium) { this.exclusivoPremium = exclusivoPremium; }
}
