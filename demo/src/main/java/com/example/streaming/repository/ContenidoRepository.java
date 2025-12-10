package com.example.streaming.repository;

import com.example.streaming.model.Contenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContenidoRepository extends JpaRepository<Contenido, Long> {

    boolean existsByCodigo(String codigo);

    // Consulta 1: Lista IDs de Contenido con más de N reproducciones
    @Query(value = "SELECT r.contenido.id, COUNT(r.id) " +
            "FROM Reproduccion r " +
            "GROUP BY r.contenido.id " +
            "HAVING COUNT(r.id) > :limite")
    List<Object[]> findContenidoIdsByReproduccionCountGreaterThan(@Param("limite") Long limite);

    // Consulta 3: Calcula el promedio de calificaciones de un Contenido
    @Query("SELECT AVG(r.calificacion) FROM Reproduccion r WHERE r.contenido.id = :contenidoId")
    Double getAverageCalificacionByContenidoId(@Param("contenidoId") Long contenidoId);
}