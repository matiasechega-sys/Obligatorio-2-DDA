package com.example.streaming.controller;

import com.example.streaming.model.TipoUsuario;
import com.example.streaming.model.Usuario;
import com.example.streaming.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // --- ENDPOINT PRINCIPAL: LISTADO Y VISTA HTML ---
    @GetMapping(produces = "text/html")
    public String listarHtml() {
        List<Usuario> usuarios = service.listar();

        StringBuilder html = new StringBuilder();
        html.append("""
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Usuarios - Streaming Backend</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
                <style>
                    body { background-color: #f8f9fa; }
                    .header-link { font-weight: bold; }
                </style>
            </head>
            <body>
        """);

        // Navbar
        html.append("""
            <nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
                <div class="container-fluid">
                    <a class="navbar-brand" href="/">🏠 Inicio API</a>
                    <div class="collapse navbar-collapse">
                        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                            <li class="nav-item">
                                <a class="nav-link active header-link" href="/usuarios">👤 Usuarios</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link header-link" href="/contenidos">🎬 Contenidos</a>
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

        // Tabla de usuarios
        html.append("""
            <div class="container mt-4">
                <h1 class="mb-4 text-primary">Gestión de Usuarios</h1>
                <p class="lead">Listado de todos los usuarios registrados en el sistema. Otros endpoints devuelven JSON.</p>
                <div class="card shadow">
                    <div class="card-header bg-primary text-white">
                        <h4 class="mb-0">Total de Usuarios: %d</h4>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-striped table-hover mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID</th>
                                        <th>Nombre Completo</th>
                                        <th>Email</th>
                                        <th>Tipo</th>
                                        <th>Descuento</th>
                                        <th>F. Registro</th>
                                        <th>F. Membresía</th>
                                    </tr>
                                </thead>
                                <tbody>
        """.formatted(usuarios.size()));

        // Filas
        for (Usuario u : usuarios) {
            String tipoClass = u.getTipo() == TipoUsuario.PREMIUM ? "badge bg-success" : "badge bg-secondary";
            String descuento = String.format("%.0f%%", u.getDescuento() * 100);

            html.append("<tr>");
            html.append("<td><a href=\"http://localhost:5000/usuarios/").append(u.getId()).append("\">")
                    .append(u.getId()).append("</a></td>");
            html.append("<td>").append(u.getNombreCompleto()).append("</td>");
            html.append("<td>").append(u.getEmail()).append("</td>");
            html.append("<td><span class=\"%s\">%s</span></td>".formatted(tipoClass, u.getTipo()));
            html.append("<td>").append(descuento).append("</td>");
            html.append("<td>").append(u.getFechaRegistro()).append("</td>");
            html.append("<td>").append(u.getInicioMembresia() != null ? u.getInicioMembresia() : "-").append("</td>");
            html.append("</tr>");
        }

        // Footer con endpoints
        html.append("""
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <div class="mt-4 p-3 border rounded bg-light">
                    <h5 class="text-secondary">Endpoints Adicionales (Respuesta JSON):</h5>
                    <ul>
                        <li><span class="badge bg-dark">GET</span> <a href="http://localhost:5000/usuarios/{id}">http://localhost:5000/usuarios/{id}</a></li>
                        <li><span class="badge bg-dark">GET</span> <a href="http://localhost:5000/usuarios/tipo/{tipo}">http://localhost:5000/usuarios/tipo/{tipo}</a></li>
                        <li><span class="badge bg-dark">GET</span> <a href="http://localhost:5000/usuarios/rango-fecha?desde=2024-01-01&hasta=2024-12-31">http://localhost:5000/usuarios/rango-fecha</a></li>
                        <li><span class="badge bg-success">POST</span> <a href="http://localhost:5000/usuarios">http://localhost:5000/usuarios</a></li>
                        <li><span class="badge bg-warning">PUT</span> <a href="http://localhost:5000/usuarios/{id}">http://localhost:5000/usuarios/{id}</a></li>
                        <li><span class="badge bg-danger">DELETE</span> <a href="http://localhost:5000/usuarios/{id}">http://localhost:5000/usuarios/{id}</a></li>
                    </ul>
                </div>

            </div>
            </body>
            </html>
        """);

        return html.toString();
    }

    // --- JSON ENDPOINTS ---

    @PostMapping(produces = "application/json")
    public ResponseEntity<Usuario> crear(@Valid @RequestBody Usuario usuario) {
        Usuario creado = service.crear(usuario);
        return ResponseEntity.status(201).body(creado);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public Usuario obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    public Usuario editar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        return service.editar(id, usuario);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/tipo/{tipo}", produces = "application/json")
    public List<Usuario> listarPorTipo(@PathVariable TipoUsuario tipo) {
        return service.listarPorTipo(tipo);
    }

    @GetMapping(value = "/rango-fecha", produces = "application/json")
    public List<Usuario> filtrarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return service.filtrarPorFechaRegistro(desde, hasta);
    }
}
