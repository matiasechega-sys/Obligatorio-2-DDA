package com.example.streaming.repository;

import com.example.streaming.model.Reproduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReproduccionRepository extends JpaRepository<Reproduccion, Long> {

    // Consulta 2: Reproducciones realizadas por un usuario específico (usada por ConsultasService)
    List<Reproduccion> findByUsuarioId(Long usuarioId);

    // Consulta 4: Reproducciones en un rango de fecha/hora (usada por ConsultasService)
    List<Reproduccion> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);
}