package com.example.streaming.controller;

import com.example.streaming.model.Contenido;
import com.example.streaming.model.Reproduccion;
import com.example.streaming.model.Usuario; 
import com.example.streaming.model.TipoUsuario; 
import com.example.streaming.service.ConsultasService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/consultas")
@CrossOrigin
public class ConsultasController {

    private final ConsultasService service;

    public ConsultasController(ConsultasService service) {
        this.service = service;
    }

    // ----------------------------------------------------
    // 1. LISTAR ENDPOINTS (GET /consultas) - HTML VIEW
    // Muestra un índice de todas las consultas disponibles.
    // ----------------------------------------------------
    @GetMapping(produces = "text/html")
    public String listarConsultasHtml() {
        StringBuilder html = new StringBuilder();
        html.append("""
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Consultas y Estadísticas - Streaming Backend</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
                <style>
                    body { background-color: #f8f9fa; }
                    .header-link { font-weight: bold; }
                    .endpoint-url { font-family: monospace; font-size: 1.1em; background-color: #e9ecef; padding: 2px 5px; border-radius: 4px; }
                </style>
            </head>
            <body>
            """);

        // 2. Barra de Navegación (Active: Consultas)
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
                                <a class="nav-link header-link" href="/reproducciones">▶ Reproducciones</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link active header-link" href="/consultas">📊 Consultas</a>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>
            """);

        // 3. Contenido Principal - Listado de Consultas
        html.append("""
            <div class="container mt-4">
                <h1 class="mb-4 text-info">📊 Estadísticas y Consultas Específicas</h1>
                <p class="lead">Esta sección lista los endpoints diseñados para obtener estadísticas y datos filtrados. Todas las respuestas son JSON.</p>
                <div class="card shadow">
                    <div class="card-header bg-info text-white">
                        <h4 class="mb-0">Endpoints de Consulta Disponibles</h4>
                    </div>
                    <ul class="list-group list-group-flush">
                        <li class="list-group-item">
                            <h5>Contenidos Populares</h5>
                            <p class="mb-1">Lista los contenidos que han sido reproducidos más de N veces.</p>
                            <p class="mb-0">
                                <span class="badge bg-dark">GET</span> 
                                <span class="endpoint-url">/consultas/contenidos-populares?n=<small>{número}</small></span>
                            </p>
                            <p class="text-muted small mt-1">Ejemplo: <a href="/consultas/contenidos-populares?n=1" class="text-decoration-none">/consultas/contenidos-populares?n=1</a></p>
                        </li>
                        <li class="list-group-item">
                            <h5>Reproducciones por Usuario</h5>
                            <p class="mb-1">Obtiene el historial completo de reproducciones de un usuario específico.</p>
                            <p class="mb-0">
                                <span class="badge bg-dark">GET</span> 
                                <span class="endpoint-url">/consultas/reproducciones-usuario/<small>{usuarioId}</small></span>
                            </p>
                            <p class="text-muted small mt-1">Ejemplo: <a href="/consultas/reproducciones-usuario/5" class="text-decoration-none">/consultas/reproducciones-usuario/5</a> (Usando el ID 5 del seeder)</p>
                        </li>
                        <li class="list-group-item">
                            <h5>Promedio de Calificaciones</h5>
                            <p class="mb-1">Calcula la calificación promedio de un contenido específico.</p>
                            <p class="mb-0">
                                <span class="badge bg-dark">GET</span> 
                                <span class="endpoint-url">/consultas/promedio-calificaciones/<small>{contenidoId}</small></span>
                            </p>
                            <p class="text-muted small mt-1">Ejemplo: <a href="/consultas/promedio-calificaciones/4" class="text-decoration-none">/consultas/promedio-calificaciones/4</a> (Usando el ID 4 del seeder)</p>
                        </li>
                        <li class="list-group-item">
                            <h5>Reproducciones por Fecha</h5>
                            <p class="mb-1">Lista todas las reproducciones realizadas en un día específico.</p>
                            <p class="mb-0">
                                <span class="badge bg-dark">GET</span> 
                                <span class="endpoint-url">/consultas/reproducciones-fecha?fecha=<small>YYYY-MM-DD</small></span>
                            </p>
                            <p class="text-muted small mt-1">Ejemplo: <a href="/consultas/reproducciones-fecha?fecha=2025-12-10" class="text-decoration-none">/consultas/reproducciones-fecha?fecha=2025-12-10</a> (Usando una fecha reciente del seeder)</p>
                        </li>
                        
                        <!-- ---------------------------------------------------- -->
                        <!-- 5. NUEVA CONSULTA: Usuarios por Tipo de Suscripción -->
                        <!-- ---------------------------------------------------- -->
                        <li class="list-group-item bg-light">
                            <h5>Usuarios por Tipo de Suscripción</h5>
                            <p class="mb-1">Lista todos los usuarios que poseen un tipo de suscripción específico (ESTANDAR o PREMIUM).</p>
                            <p class="mb-0">
                                <span class="badge bg-dark">GET</span> 
                                <span class="endpoint-url">/consultas/usuarios-por-tipo/<small>{tipo}</small></span>
                            </p>
                            <p class="text-muted small mt-1">Ejemplo: <a href="/consultas/usuarios-por-tipo/PREMIUM" class="text-decoration-none">/consultas/usuarios-por-tipo/PREMIUM</a> (Valores posibles: ESTANDAR o PREMIUM)</p>
                        </li>
                    </ul>
                </div>
            </div>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
            </body>
            </html>
            """);

        return html.toString();
    }


    // ----------------------------------------------------
    // 2. GET /consultas/contenidos-populares?n=10 (JSON)
    // ----------------------------------------------------
    @GetMapping(value = "/contenidos-populares", produces = "application/json")
    public List<Contenido> listarContenidosConMasDeNReproducciones(@RequestParam(defaultValue = "0") Long n) {
        return service.listarContenidosConMasDeNReproducciones(n);
    }

    // ----------------------------------------------------
    // 3. GET /consultas/reproducciones-usuario/{usuarioId} (JSON)
    // ----------------------------------------------------
    @GetMapping(value = "/reproducciones-usuario/{usuarioId}", produces = "application/json")
    public List<Reproduccion> listarReproduccionesPorUsuario(@PathVariable Long usuarioId) {
        return service.listarReproduccionesPorUsuario(usuarioId);
    }

    // ----------------------------------------------------
    // 4. GET /consultas/promedio-calificaciones/{contenidoId} (JSON)
    // ----------------------------------------------------
    @GetMapping(value = "/promedio-calificaciones/{contenidoId}", produces = "application/json")
    public ResponseEntity<Double> calcularPromedioCalificaciones(@PathVariable Long contenidoId) {
        Double promedio = service.calcularPromedioCalificaciones(contenidoId);
        return ResponseEntity.ok(promedio != null ? promedio : 0.0);
    }

    // ----------------------------------------------------
    // 5. GET /consultas/reproducciones-fecha?fecha=YYYY-MM-DD (JSON)
    // ----------------------------------------------------
    @GetMapping(value = "/reproducciones-fecha", produces = "application/json")
    public List<Reproduccion> listarReproduccionesPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return service.listarReproduccionesPorFecha(fecha);
    }
    
    // ----------------------------------------------------
    // 6. GET /consultas/usuarios-por-tipo/{tipo} (JSON)
    // Implementa la Consulta 5, usando el Enum TipoUsuario.
    // ----------------------------------------------------
    @GetMapping(value = "/usuarios-por-tipo/{tipo}", produces = "application/json")
    public List<Usuario> listarUsuariosPorTipo(@PathVariable TipoUsuario tipo) {
        return service.listarUsuariosPorTipo(tipo);
    }
}