package com.example.streaming.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Entity
public class Reproduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Usuario usuario;

    @ManyToOne(optional = false)
    private Contenido contenido;

    @Positive(message = "La duración reproducida debe ser mayor a 0")
    private int duracionReproducida;

    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private int calificacion;

    private LocalDateTime fechaHora;

    public Reproduccion() {}

    public Long getId() { return id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Contenido getContenido() { return contenido; }
    public void setContenido(Contenido contenido) { this.contenido = contenido; }

    public int getDuracionReproducida() { return duracionReproducida; }
    public void setDuracionReproducida(int duracionReproducida) { this.duracionReproducida = duracionReproducida; }

    public int getCalificacion() { return calificacion; }
    public void setCalificacion(int calificacion) { this.calificacion = calificacion; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}
