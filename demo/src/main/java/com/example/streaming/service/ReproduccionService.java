package com.example.streaming.service;

import com.example.streaming.exception.NotFoundException;
import com.example.streaming.model.*;
import com.example.streaming.repository.ReproduccionRepository;
import com.example.streaming.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReproduccionService {

    private final ReproduccionRepository reproRepo;
    private final UsuarioRepository usuarioRepo;
    private final ContenidoService contenidoService;

    public ReproduccionService(ReproduccionRepository reproRepo,
                               UsuarioRepository usuarioRepo,
                               ContenidoService contenidoService) {
        this.reproRepo = reproRepo;
        this.usuarioRepo = usuarioRepo;
        this.contenidoService = contenidoService;
    }

    // --- REGISTRAR REPRODUCCIÓN (POST) ---
    public Reproduccion registrarReproduccion(Long usuarioId, Long contenidoId,
                                              int duracion, int calificacion) {

        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario con ID " + usuarioId + " no encontrado"));

        Contenido contenido = contenidoService.obtenerPorId(contenidoId);

        // Validar acceso
        contenidoService.validarAcceso(usuarioId, contenidoId);

        // Validación de duración
        if (duracion < 0) {
            throw new IllegalArgumentException("Duración reproducida inválida. Debe ser positiva o cero.");
        }

        // Validación de calificación
        if (calificacion < 1 || calificacion > 5) {
            throw new IllegalArgumentException("La calificación debe ser un número entre 1 y 5.");
        }

        Reproduccion r = new Reproduccion();
        r.setUsuario(usuario);
        r.setContenido(contenido);
        r.setDuracionReproducida(duracion);
        r.setCalificacion(calificacion);
        r.setFechaHora(LocalDateTime.now());

        return reproRepo.save(r);
    }

    // --- ACTUALIZAR REPRODUCCIÓN (PUT) ---
    public Reproduccion actualizarReproduccion(Long id, Reproduccion detalles) {

        Reproduccion existente = obtenerPorId(id);

        Long contenidoIdNuevo =
                (detalles.getContenido() != null && detalles.getContenido().getId() != null)
                        ? detalles.getContenido().getId()
                        : existente.getContenido().getId();

        Long usuarioId = existente.getUsuario().getId();

        // Revalidar acceso
        contenidoService.validarAcceso(usuarioId, contenidoIdNuevo);

        // Obtener contenido para validaciones
        Contenido contenidoValidar = contenidoService.obtenerPorId(contenidoIdNuevo);

        // Validación de duración
        if (detalles.getDuracionReproducida() < 0) {
            throw new IllegalArgumentException("Duración reproducida inválida. Debe ser positiva o cero.");
        }

        // Validación de calificación
        if (detalles.getCalificacion() < 1 || detalles.getCalificacion() > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5.");
        }

        // Actualizar contenido si cambió
        if (!existente.getContenido().getId().equals(contenidoIdNuevo)) {
            existente.setContenido(contenidoValidar);
        }

        // Actualizar datos simples
        existente.setDuracionReproducida(detalles.getDuracionReproducida());
        existente.setCalificacion(detalles.getCalificacion());

        return reproRepo.save(existente);
    }

    // --- MÉTODOS DE CONSULTA Y ELIMINACIÓN ---

    public List<Reproduccion> listarTodos() {
        return reproRepo.findAll();
    }

    public Reproduccion obtenerPorId(Long id) {
        return reproRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Reproducción con ID " + id + " no encontrada"));
    }

    public void eliminar(Long id) {
        if (!reproRepo.existsById(id)) {
            throw new NotFoundException("Reproducción con ID " + id + " no encontrada");
        }
        reproRepo.deleteById(id);
    }

    public List<Reproduccion> listarPorUsuario(Long usuarioId) {
        return reproRepo.findByUsuarioId(usuarioId);
    }

    public List<Reproduccion> obtenerReproduccionesPorRangoFecha(LocalDateTime inicio, LocalDateTime fin) {
        return reproRepo.findByFechaHoraBetween(inicio, fin);
    }
}
