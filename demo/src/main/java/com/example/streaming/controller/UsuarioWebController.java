package com.example.streaming.controller;

import com.example.streaming.model.Usuario;
import com.example.streaming.model.TipoUsuario;
import com.example.streaming.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioWebController {

    @Autowired
    private UsuarioService usuarioService;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping(produces = "text/html")
    @ResponseBody
    public String listarUsuarios(@ModelAttribute("mensaje") String mensaje,
                                 @ModelAttribute("error") String error) {

        List<Usuario> usuarios = usuarioService.listar();

        long premiumCount = usuarios.stream()
                .filter(u -> u.getTipo() == TipoUsuario.PREMIUM)
                .count();
        long standardCount = usuarios.size() - premiumCount;

        StringBuilder html = new StringBuilder();
        html.append(getHtmlHeader("Gestión de Usuarios",
                ".header-link[href='/admin/usuarios'] { font-weight: bold; }"));
        html.append(getNavbarHtml("Usuarios"));

        html.append("<div class='container mt-4'>");
        html.append("<h1 class='mb-4 text-primary'>👤 Gestión de Usuarios (ABM)</h1>");

        if (mensaje != null && !mensaje.isBlank()) {
            html.append("""
                <div class='alert alert-success alert-dismissible fade show'>
                    %s
                    <button type='button' class='btn-close' data-bs-dismiss='alert'></button>
                </div>
            """.formatted(mensaje));
        }

        if (error != null && !error.isBlank()) {
            html.append("""
                <div class='alert alert-danger alert-dismissible fade show'>
                    %s
                    <button type='button' class='btn-close' data-bs-dismiss='alert'></button>
                </div>
            """.formatted(error));
        }

        html.append("<div class='d-flex justify-content-end mb-3'>");
        html.append("<a href='/admin/usuarios/nuevo' class='btn btn-success shadow-sm'>➕ Crear Nuevo Usuario</a>");
        html.append("</div>");

        html.append("<div class='card shadow'>");
        html.append("<div class='card-header bg-primary text-white'><h4 class='mb-0'>Total: ")
                .append(usuarios.size()).append("</h4></div>");

        html.append("<div class='card-body p-0'><div class='table-responsive'>");

        html.append("""
            <table class='table table-striped table-hover mb-0'>
                <thead class='table-light'>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Email</th>
                        <th>Tipo</th>
                        <th>Registro</th>
                        <th>Inicio Memb.</th>
                        <th>Desc.</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
        """);

        html.append("<tbody>");

        for (Usuario u : usuarios) {

            String tipoBadge = u.getTipo() == TipoUsuario.PREMIUM
                    ? "<span class='badge bg-success'>PREMIUM</span>"
                    : "<span class='badge bg-secondary'>STANDARD</span>";

            String fechaReg = u.getFechaRegistro() != null
                    ? u.getFechaRegistro().format(DATE_FORMATTER)
                    : "-";

            String inicioMem = u.getInicioMembresia() != null
                    ? u.getInicioMembresia().format(DATE_FORMATTER)
                    : "-";

            String descuento = String.format("%.0f%%", u.getDescuento() * 100);

            html.append("<tr>");
            html.append("<td>").append(u.getId()).append("</td>");
            html.append("<td>").append(u.getNombreCompleto()).append("</td>");
            html.append("<td>").append(u.getEmail()).append("</td>");
            html.append("<td>").append(tipoBadge).append("</td>");
            html.append("<td>").append(fechaReg).append("</td>");
            html.append("<td>").append(inicioMem).append("</td>");
            html.append("<td><span class='badge bg-info'>").append(descuento).append("</span></td>");

            html.append("<td>");
            html.append("<a href='/admin/usuarios/editar/").append(u.getId())
                    .append("' class='btn btn-sm btn-warning me-2'>✏️ Editar</a>");
            html.append("<a href='/admin/usuarios/eliminar/").append(u.getId())
                    .append("' class='btn btn-sm btn-danger' onclick='return confirm(\"¿Seguro?\");'>❌ Eliminar</a>");
            html.append("</td>");

            html.append("</tr>");
        }

        html.append("</tbody></table></div></div></div>");
        
        html.append("""
            <div class='row mt-4 mb-4'>
                <div class='col-md-6'>
                    <div class='card text-white bg-secondary shadow-sm'>
                        <div class='card-body'>
                            <h5 class='card-title'>Usuarios Estandar🧍</h5>
                            <p class='card-text fs-2'>%d</p>
                        </div>
                    </div>
                </div>
                <div class='col-md-6'>
                    <div class='card text-white bg-success shadow-sm'>
                        <div class='card-body'>
                            <h5 class='card-title'>Usuarios Premium 👑</h5>
                            <p class='card-text fs-2'>%d</p>
                        </div>
                    </div>
                </div>
            </div>
        """.formatted(standardCount, premiumCount));

        html.append("</div>"); 
        html.append(getHtmlFooter());

        return html.toString();
    }

    @GetMapping({"/nuevo", "/editar/{id}"})
    @ResponseBody
    public String mostrarFormulario(@PathVariable(required = false) Long id) {

        Usuario u = (id != null) ? usuarioService.obtener(id) : new Usuario();

        String titulo = (id != null)
                ? "Editar Usuario (ID: " + id + ")"
                : "Nuevo Usuario";

        StringBuilder html = new StringBuilder();
        html.append(getHtmlHeader(titulo,
                ".header-link[href='/admin/usuarios']{font-weight:bold;}"));
        html.append(getNavbarHtml("Usuarios"));

        html.append("<div class='container mt-4'>");
        html.append("<h1 class='mb-4 text-success'>").append(titulo).append("</h1>");

        html.append("<div class='card shadow'><div class='card-body'>");

        html.append("<form action='/admin/usuarios/guardar' method='post'>");

        html.append("<input type='hidden' name='id' value='")
                .append(u.getId() != null ? u.getId() : "")
                .append("'>");

        html.append(createInputField("text", "nombreCompleto", "Nombre Completo",
                u.getNombreCompleto(), true));

        html.append(createInputField("email", "email", "Email",
                u.getEmail(), true));

        html.append("""
            <div class='mb-3'>
                <label class='form-label'>Tipo de Usuario</label>
                <select class='form-select' name='tipo'>
        """);

        for (TipoUsuario tipo : TipoUsuario.values()) {
            html.append("<option value='")
                    .append(tipo)
                    .append("' ")
                    .append((u.getTipo() != null && u.getTipo() == tipo) ? "selected" : "")
                    .append(">")
                    .append(tipo)
                    .append("</option>");
        }

        html.append("</select></div>");

        html.append("<button class='btn btn-success me-2'>Guardar</button>");
        html.append("<a href='/admin/usuarios' class='btn btn-secondary'>Cancelar</a>");

        html.append("</form></div></div></div>");
        html.append(getHtmlFooter());

        return html.toString();
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam(required = false) Long id,
            @RequestParam String nombreCompleto,
            @RequestParam String email,
            @RequestParam TipoUsuario tipo,
            RedirectAttributes ra) {

        Usuario u = (id != null) ? usuarioService.obtener(id) : new Usuario();

        if (id == null) {
            u.setFechaRegistro(LocalDate.now());
        }

        u.setNombreCompleto(nombreCompleto);
        u.setEmail(email);
        u.setTipo(tipo);

        if (id == null) {
            usuarioService.crear(u);
            ra.addFlashAttribute("mensaje", "Usuario creado correctamente.");
        } else {
            usuarioService.editar(id, u);
            ra.addFlashAttribute("mensaje", "Usuario modificado correctamente.");
        }

        return "redirect:/admin/usuarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        usuarioService.eliminar(id);
        ra.addFlashAttribute("mensaje", "Usuario eliminado correctamente.");
        return "redirect:/admin/usuarios";
    }


    private String getHtmlHeader(String title, String customStyle) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
                <style>
                    body { background-color: #f8f9fa; }
                    .header-link { font-weight: bold; }
                    %s
                </style>
            </head>
            <body>
        """.formatted(title, customStyle);
    }

    private String getNavbarHtml(String activeModule) {
        String usuariosClass = activeModule.equals("Usuarios") ? "active header-link" : "header-link";
        String contenidosClass = activeModule.equals("Contenidos") ? "active header-link" : "header-link";
        String reproduccionesClass = activeModule.equals("Reproducciones") ? "active header-link" : "header-link";

        return """
            <nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
                <div class="container-fluid">
                    <a class="navbar-brand" href="/">🏠 Inicio API</a>
                    <div class="collapse navbar-collapse">
                        <ul class="navbar-nav me-auto">
                            <li class="nav-item"><a class="nav-link %s" href="/admin/usuarios">👤 Usuarios</a></li>
                            <li class="nav-item"><a class="nav-link %s" href="/admin/contenidos">🎬 Contenidos</a></li>
                            <li class="nav-item"><a class="nav-link %s" href="/admin/reproducciones">▶ Reproducciones</a></li>
                            <li class="nav-item"><a class="nav-link header-link" href="/consultas">📊 Consultas</a></li>
                        </ul>
                    </div>
                </div>
            </nav>
        """.formatted(usuariosClass, contenidosClass, reproduccionesClass);
    }

    private String createInputField(String type, String name, String label, String value, boolean required) {
        String requiredAttr = required ? "required" : "";
        return """
            <div class='mb-3'>
                <label class='form-label' for='%s'>%s</label>
                <input type='%s' id='%s' name='%s' class='form-control' value='%s' %s>
            </div>
        """.formatted(name, label, type, name, name, value != null ? value : "", requiredAttr);
    }

    private String getHtmlFooter() {
        return """
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
            </body>
            </html>
        """;
    }
}