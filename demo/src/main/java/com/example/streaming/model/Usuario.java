package com.example.streaming.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects; 

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres")
    private String nombreCompleto;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    @Column(unique = true)
    private String email;

    @NotNull(message = "La fecha de registro es obligatoria")
    @PastOrPresent(message = "La fecha no puede ser futura")
    private LocalDate fechaRegistro;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El tipo de usuario es obligatorio")
    private TipoUsuario tipo;

    
    private LocalDate inicioMembresia;
    private Double descuento;

    public Usuario() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public TipoUsuario getTipo() { return tipo; }
    public void setTipo(TipoUsuario tipo) { this.tipo = tipo; }

    public LocalDate getInicioMembresia() { return inicioMembresia; }
    public void setInicioMembresia(LocalDate inicioMembresia) { this.inicioMembresia = inicioMembresia; }

    public Double getDescuento() { 
        return Objects.requireNonNullElse(descuento, 0.0);
    }
    public void setDescuento(Double descuento) { this.descuento = descuento; }
}