package com.example.streaming.controller;

import com.example.streaming.model.Reproduccion;
import com.example.streaming.service.ReproduccionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/reproducciones")
@CrossOrigin
public class ReproduccionController {

    private final ReproduccionService reproService;


    public ReproduccionController(ReproduccionService reproService) {
        this.reproService = reproService;
    }

    // -------------------------------
    // 1. CREAR (POST) - USANDO LA ENTIDAD EN @RequestBody
    // -------------------------------
    @PostMapping(produces = "application/json")
    public ResponseEntity<Reproduccion> registrarReproduccion(@RequestBody Reproduccion reproduccionSolicitud) {

        // 🚨 CAMBIO CRÍTICO: Extraemos los IDs de los objetos anidados (Usuario y Contenido)
        // Spring mapeará el JSON a la Entidad, pero solo los IDs estarán disponibles en los objetos anidados.
        
        // Verificamos que los objetos anidados existan y tengan ID antes de intentar extraerlos.
        if (reproduccionSolicitud.getUsuario() == null || reproduccionSolicitud.getUsuario().getId() == null) {
             throw new IllegalArgumentException("El campo 'usuario' con su 'id' es obligatorio.");
        }
        if (reproduccionSolicitud.getContenido() == null || reproduccionSolicitud.getContenido().getId() == null) {
             throw new IllegalArgumentException("El campo 'contenido' con su 'id' es obligatorio.");
        }
        
        Long usuarioId = reproduccionSolicitud.getUsuario().getId();
        Long contenidoId = reproduccionSolicitud.getContenido().getId();
        int duracion = reproduccionSolicitud.getDuracionReproducida();
        int calificacion = reproduccionSolicitud.getCalificacion();

        // ⚠️ Nota: Se asume que el Service lanzará excepciones (403, 400, 404) si hay errores.
        Reproduccion nueva = reproService.registrarReproduccion(
                usuarioId, contenidoId, duracion, calificacion
        );

        return new ResponseEntity<>(nueva, HttpStatus.CREATED);
    }

    // -------------------------------
    // 2. LISTAR TODOS - HTML VIEW
    // -------------------------------
    @GetMapping(produces = "text/html")
    public String listarHtml() {
        List<Reproduccion> reproducciones = reproService.listarTodos();

        // 1. Inicia la estructura HTML con Bootstrap
        StringBuilder html = new StringBuilder();
        html.append("""
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Reproducciones - Streaming Backend</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
                <style>
                    body { background-color: #f8f9fa; }
                    .header-link { font-weight: bold; }
                    .rating-star { color: #ffc107; }
                </style>
            </head>
            <body>
            """);

        // 2. Barra de Navegación (Active: Reproducciones)
        html.append("""
            <nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
                <div class="container-fluid">
                    <a class="navbar-brand" href="/">🏠 Inicio API</a>
                    <div class="collapse navbar-collapse">
                        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                            <li class="nav-item">
                                <a class="nav-link header-link" href="/usuarios">👤 Usuarios</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link header-link" href="/contenidos">🎬 Contenidos</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link active header-link" href="/api/reproducciones">▶ Reproducciones</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link header-link" href="/consultas">📊 Consultas</a>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>
            """);

        // 3. Contenido Principal
        html.append("""
            <div class="container mt-4">
                <h1 class="mb-4 text-success">Registro de Reproducciones</h1>
                <p class="lead">Historial de visualizaciones registradas por los usuarios. Otros endpoints devuelven JSON.</p>
                <div class="card shadow">
                    <div class="card-header bg-success text-white">
                        <h4 class="mb-0">Total de Registros: %d</h4>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-striped table-hover mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID</th>
                                        <th>ID Usuario</th>
                                        <th>ID Contenido</th>
                                        <th>Fecha / Hora</th>
                                        <th>Duración (seg)</th>
                                        <th>Calificación (1-5)</th>
                                    </tr>
                                </thead>
                                <tbody>
            """.formatted(reproducciones.size()));

        // 4. Llenar la tabla con los datos de las reproducciones
        for (Reproduccion r : reproducciones) {
            String calificacionStars = formatRating(r.getCalificacion());

            Long usuarioId = r.getUsuario() != null ? r.getUsuario().getId() : null;
            Long contenidoId = r.getContenido() != null ? r.getContenido().getId() : null;

            html.append("<tr>");
            html.append("<td>").append(r.getId()).append("</td>");
            html.append("<td><a href=\"/usuarios/").append(usuarioId).append("\">").append(usuarioId).append("</a></td>");
            html.append("<td><a href=\"/contenidos/").append(contenidoId).append("\">").append(contenidoId).append("</a></td>");
            html.append("<td>").append(r.getFechaHora()).append("</td>");
            html.append("<td>").append(r.getDuracionReproducida()).append("</td>");
            html.append("<td>").append(calificacionStars).append("</td>");
            html.append("</tr>");
        }

        // 5. Cierre de la estructura HTML y utilidades
        html.append("""
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <div class="mt-4 p-3 border rounded bg-light">
                    <h5 class="text-secondary">Endpoints Adicionales (Respuesta JSON):</h5>
                    <ul>
<li><span class=\"badge bg-dark\">GET</span> <a href=\"http://localhost:5000/reproducciones/{id}\">http://localhost:5000/reproducciones/{id}</a></li>
<li><span class=\"badge bg-dark\">GET</span> <a href=\"http://localhost:5000/reproducciones/usuario/{id}\">http://localhost:5000/reproducciones/usuario/{id}</a></li>
<li><span class=\"badge bg-dark\">GET</span> <a href=\"http://localhost:5000/reproducciones/rango?inicio=YYYY-MM-DDTHH:MM:SS&fin=YYYY-MM-DDTHH:MM:SS\">http://localhost:5000/reproducciones/rango?inicio=YYYY-MM-DDTHH:MM:SS&fin=...</a></li>
<li><span class=\"badge bg-success\">POST</span> <a href=\"http://localhost:5000/reproducciones\">http://localhost:5000/reproducciones</a></li>
<li><span class=\"badge bg-warning\">PUT</span> <a href=\"http://localhost:5000/reproducciones/{id}\">http://localhost:5000/reproducciones/{id}</a></li>
<li><span class=\"badge bg-danger\">DELETE</span> <a href=\"http://localhost:5000/reproducciones/{id}\">http://localhost:5000/reproducciones/{id}</a></li>
</ul>
                </div>
            </div>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
            </body>
            </html>
            """);

        return html.toString();
    }
    
    // Helper para formatear la calificación
    private String formatRating(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                stars.append("<span class=\"rating-star\">★</span>"); // Estrella llena
            } else {
                stars.append("<span>☆</span>"); // Estrella vacía
            }
        }
        return stars.toString();
    }

    // -------------------------------
    // 3. OBTENER POR ID - JSON RESPONSE
    // -------------------------------
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Reproduccion> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reproService.obtenerPorId(id));
    }

    // -------------------------------
    // 4. ACTUALIZAR - USANDO LA ENTIDAD EN @RequestBody
    // -------------------------------
    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Reproduccion> actualizar(
            @PathVariable Long id,
            @RequestBody Reproduccion body) {
        
        // Se asume que el Service validará los campos antes de actualizar
        return ResponseEntity.ok(reproService.actualizarReproduccion(id, body));
    }

    // -------------------------------
    // 5. ELIMINAR - JSON RESPONSE
    // -------------------------------
    @DeleteMapping(value = "/{id}", produces = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        reproService.eliminar(id);
    }

    // -------------------------------
    // 6. LISTAR POR USUARIO - JSON RESPONSE
    // -------------------------------
    @GetMapping(value = "/usuario/{id}", produces = "application/json")
    public List<Reproduccion> listarPorUsuario(@PathVariable Long id) {
        return reproService.listarPorUsuario(id);
    }

    // -------------------------------
    // 7. RANGO DE FECHAS - JSON RESPONSE
    // -------------------------------
    @GetMapping(value = "/rango", produces = "application/json")
    public ResponseEntity<?> listarPorRangoFecha(
            @RequestParam String inicio,
            @RequestParam String fin) {

        LocalDateTime ini, fn;
        try {
            ini = LocalDateTime.parse(inicio);
            fn = LocalDateTime.parse(fin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Formato de fecha/hora inválido. Use: yyyy-MM-ddTHH:mm:ss"));
        }

        return ResponseEntity.ok(reproService.obtenerReproduccionesPorRangoFecha(ini, fn));
    }
}