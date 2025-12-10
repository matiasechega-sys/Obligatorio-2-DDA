package com.example.streaming.service;

import com.example.streaming.model.Contenido;
import com.example.streaming.model.Reproduccion;
import com.example.streaming.model.Usuario;
import com.example.streaming.model.TipoUsuario;
import com.example.streaming.repository.ContenidoRepository;
import com.example.streaming.repository.ReproduccionRepository;
import com.example.streaming.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultasService {

    private final ContenidoRepository contenidoRepo;
    private final ReproduccionRepository reproduccionRepo;
    private final UsuarioRepository usuarioRepo; 

    // Constructor actualizado con UsuarioRepository
    public ConsultasService(
        ContenidoRepository contenidoRepo, 
        ReproduccionRepository reproduccionRepo,
        UsuarioRepository usuarioRepo // Inyección
    ) {
        this.contenidoRepo = contenidoRepo;
        this.reproduccionRepo = reproduccionRepo;
        this.usuarioRepo = usuarioRepo; // Asignación
    }

    // --- Consultas Existentes ---

    // Consulta 1: Contenidos con más de N reproducciones
    @Transactional(readOnly = true)
    public List<Contenido> listarContenidosConMasDeNReproducciones(Long n) {
        List<Long> contenidoIds = contenidoRepo.findContenidoIdsByReproduccionCountGreaterThan(n).stream()
                .map(result -> (Long) result[0])
                .collect(Collectors.toList());

        return contenidoRepo.findAllById(contenidoIds);
    }

    // Consulta 2: Reproducciones de un usuario
    @Transactional(readOnly = true)
    public List<Reproduccion> listarReproduccionesPorUsuario(Long usuarioId) {
        return reproduccionRepo.findByUsuarioId(usuarioId);
    }

    // Consulta 3: Promedio de calificaciones de un contenido
    @Transactional(readOnly = true)
    public Double calcularPromedioCalificaciones(Long contenidoId) {
        return contenidoRepo.getAverageCalificacionByContenidoId(contenidoId);
    }

    // Consulta 4: Reproducciones en una fecha específica
    @Transactional(readOnly = true)
    public List<Reproduccion> listarReproduccionesPorFecha(LocalDate fecha) {
        LocalDateTime inicioDia = LocalDateTime.of(fecha, LocalTime.MIN);
        // Usar LocalTime.MAX garantiza incluir todo el día hasta el último nanosegundo.
        LocalDateTime finDia = LocalDateTime.of(fecha, LocalTime.MAX); 

        return reproduccionRepo.findByFechaHoraBetween(inicioDia, finDia);
    }

    
    // =========================================================
    // ⭐ CONSULTA B (5): USUARIOS POR TIPO Y FECHA (Implementación corregida) ⭐
    // =========================================================
    
    /**
     * Filtra usuarios por su tipo de suscripción y rango de fecha de registro.
     * Este método sustituye y mejora la funcionalidad de `listarUsuariosPorTipo`.
     * * NOTA: Requiere que `UsuarioRepository` tenga el método 
     * `findByFiltrosMultiples(TipoUsuario tipo, LocalDate fechaDesde, LocalDate fechaHasta)`
     * que usa @Query para manejar los nulls.
     * * @param tipo El tipo de usuario (opcional: puede ser null).
     * @param fechaDesde La fecha mínima de registro (opcional: puede ser null).
     * @param fechaHasta La fecha máxima de registro (opcional: puede ser null).
     * @return Lista de usuarios que cumplen todos los criterios aplicados.
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuario(
            TipoUsuario tipo,
            LocalDate fechaDesde,
            LocalDate fechaHasta) {
        
        // Llama al método avanzado del repositorio (findByFiltrosMultiples)
        return usuarioRepo.findByFiltrosMultiples(tipo, fechaDesde, fechaHasta);
    }
    
    // Mantenemos el antiguo método, pero lo delegamos al nuevo, 
    // en caso de que otros controladores o servicios lo sigan usando.
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuariosPorTipo(TipoUsuario tipo) {
         // Reutilizamos la lógica de la función avanzada, pasando null en las fechas
         return listarUsuario(tipo, null, null); 
    }
}