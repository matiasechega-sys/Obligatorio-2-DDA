package com.example.streaming.controller;

import com.example.streaming.model.Contenido;
import com.example.streaming.service.ContenidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contenidos") 
@CrossOrigin 
public class ContenidoController {

    private final ContenidoService contenidoService;

    public ContenidoController(ContenidoService contenidoService) {
        this.contenidoService = contenidoService;
    }

    @PostMapping(produces = "application/json")
    public Contenido crear(@RequestBody Contenido contenido) {
        return contenidoService.crearContenido(contenido);
    }

    // --- 2. LISTAR TODOS (GET) - HTML VIEW ---
    // URL: GET /contenidos
    @GetMapping(produces = "text/html")
    public String listarHtml() {
        List<Contenido> contenidos = contenidoService.listarContenidos();

        // 1. Inicia la estructura HTML con Bootstrap
        StringBuilder html = new StringBuilder();
        html.append("""
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Contenidos - Streaming Backend</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
                <style>
                    body { background-color: #f8f9fa; }
                    .header-link { font-weight: bold; }
                    .premium-tag { font-weight: 600; }
                </style>
            </head>
            <body>
            """);

        // 2. Barra de Navegación (Active: Contenidos)
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
                                <a class="nav-link active header-link" href="/contenidos">🎬 Contenidos</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link header-link" href="/reproducciones">▶ Reproducciones</a>
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
                <h1 class="mb-4 text-warning">Gestión de Contenidos (Películas y Series)</h1>
                <p class="lead">Listado completo del catálogo de contenidos. Otros endpoints devuelven JSON.</p>
                <div class="card shadow">
                    <div class="card-header bg-warning text-dark">
                        <h4 class="mb-0">Total de Títulos: %d</h4>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-striped table-hover mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID</th>
                                        <th>Título</th>
                                        <th>Código</th>
                                        <th>Categoría</th>
                                        <th>Duración (min)</th>
                                        <th>Año</th>
                                        <th>Exclusivo Premium</th>
                                        <th>Precio Venta (USD)</th>
                                    </tr>
                                </thead>
                                <tbody>
            """.formatted(contenidos.size()));

        // 4. Llenar la tabla con los datos de los contenidos
        for (Contenido c : contenidos) {
            String premiumBadge = c.isExclusivoPremium()
                    ? "<span class=\"badge bg-danger premium-tag\">Sí</span>"
                    : "<span class=\"badge bg-info text-dark\">No</span>";

            html.append("<tr>");
            html.append("<td>").append(c.getId()).append("</td>");
            html.append("<td><a href=\"/contenidos/").append(c.getId()).append("\">").append(c.getTitulo()).append("</a></td>");
            html.append("<td>").append(c.getCodigo()).append("</td>");
            html.append("<td>").append(c.getCategoria()).append("</td>");
            html.append("<td>").append(c.getDuracion()).append("</td>");
            html.append("<td>").append(c.getAnio()).append("</td>");
            html.append("<td>").append(premiumBadge).append("</td>");
            html.append("<td>").append(String.format("%.2f", c.getPrecio())).append("</td>");
            html.append("</tr>");
        }

        // 5. Cierre de la estructura HTML y utilidades
        html.append("""
</tbody>
</table>
</div>
</div>
</div>
<div class=\"mt-4 p-3 border rounded bg-light\">
<h5 class=\"text-secondary\">Endpoints Adicionales (Respuesta JSON):</h5>
<ul>
<li><span class=\"badge bg-dark\">GET</span> <a href=\"http://localhost:5000/contenidos/{id}\">http://localhost:5000/contenidos/{id}</a></li>
<li><span class=\"badge bg-success\">POST</span> <a href=\"http://localhost:5000/contenidos\">http://localhost:5000/contenidos</a></li>
<li><span class=\"badge bg-warning\">PUT</span> <a href=\"http://localhost:5000/contenidos/{id}\">http://localhost:5000/contenidos/{id}</a></li>
<li><span class=\"badge bg-danger\">DELETE</span> <a href=\"http://localhost:5000/contenidos/{id}\">http://localhost:5000/contenidos/{id}</a></li>
</ul>
</div>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
            </body>
            </html>
            """);

        return html.toString();
    }

    // --- 3. OBTENER POR ID (GET) - JSON RESPONSE ---
    // URL: GET /contenidos/{id}
    @GetMapping(value = "/{id}", produces = "application/json")
    public Contenido obtener(@PathVariable Long id) {
        // Llama al método del servicio que lanza NotFoundException si no existe
        return contenidoService.obtenerPorId(id);
    }

    // ⭐ 4. ACTUALIZAR (PUT) - JSON RESPONSE ⭐
    // URL: PUT /contenidos/{id}
    @PutMapping(value = "/{id}", produces = "application/json")
    public Contenido actualizar(@PathVariable Long id, @RequestBody Contenido detallesContenido) {
        // Llama al método del servicio que maneja la actualización
        return contenidoService.actualizarContenido(id, detallesContenido);
    }

    // --- 5. ELIMINAR (DELETE) ---
    // URL: DELETE /contenidos/{id}
    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        // Llama al método del servicio que maneja la eliminación
        contenidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}