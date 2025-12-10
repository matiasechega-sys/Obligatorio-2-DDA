package com.example.streaming.repository;

import com.example.streaming.model.Usuario;
import com.example.streaming.model.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Importación necesaria
import org.springframework.data.repository.query.Param; // Importación necesaria

import java.time.LocalDate;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);

    List<Usuario> findByFechaRegistroBetween(LocalDate inicio, LocalDate fin);

    List<Usuario> findByTipo(TipoUsuario tipo);
    
    // ⭐ MÉTODO AÑADIDO/CORREGIDO (Para la Consulta B) ⭐
    // Esta consulta JPQL maneja los tres filtros de forma opcional.
    // Si un parámetro es NULL, la condición se vuelve true, ignorando ese filtro.
    @Query("SELECT u FROM Usuario u WHERE " +
           "(:tipo IS NULL OR u.tipo = :tipo) AND " +
           "(:fechaDesde IS NULL OR u.fechaRegistro >= :fechaDesde) AND " +
           "(:fechaHasta IS NULL OR u.fechaRegistro <= :fechaHasta)")
    List<Usuario> findByFiltrosMultiples(
        @Param("tipo") TipoUsuario tipo, 
        @Param("fechaDesde") LocalDate fechaDesde, 
        @Param("fechaHasta") LocalDate fechaHasta
    );
}