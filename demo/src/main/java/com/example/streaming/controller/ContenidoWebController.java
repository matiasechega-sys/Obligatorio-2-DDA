package com.example.streaming.controller;

import com.example.streaming.exception.NotFoundException;
import com.example.streaming.model.Contenido;
import com.example.streaming.service.ContenidoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/contenidos")
public class ContenidoWebController {

    @Autowired
    private ContenidoService contenidoService;

    @GetMapping(produces = "text/html")
    @ResponseBody
    public String listarContenidos(Model model) {

        List<Contenido> contenidos = contenidoService.listarContenidos();

        StringBuilder html = new StringBuilder();
        html.append(getHtmlHeader("Gestión de Contenidos",
                ".header-link[href='/admin/contenidos'] { font-weight: bold; }"));
        html.append(getNavbarHtml("Contenidos"));

        html.append("<div class='container mt-4'>");
        html.append("<h1 class='mb-4 text-primary'>🎬 Gestión de Contenidos (ABM)</h1>");

        Object mensaje = model.getAttribute("mensaje");
        Object error = model.getAttribute("error");

        if (mensaje != null) {
            html.append("""
                <div class='alert alert-success alert-dismissible fade show'>
                    %s
                    <button type='button' class='btn-close' data-bs-dismiss='alert'></button>
                </div>
            """.formatted(mensaje));
        }

        if (error != null) {
            html.append("""
                <div class='alert alert-danger alert-dismissible fade show'>
                    %s
                    <button type='button' class='btn-close' data-bs-dismiss='alert'></button>
                </div>
            """.formatted(error));
        }

        html.append("<div class='d-flex justify-content-end mb-3'>");
        html.append("<a href='/admin/contenidos/nuevo' class='btn btn-success shadow-sm'>➕ Nuevo Contenido</a>");
        html.append("</div>");

        html.append("<div class='card shadow'>");
        html.append("<div class='card-header bg-primary text-white'><h4 class='mb-0'>Total: ")
                .append(contenidos.size()).append("</h4></div>");

        html.append("<div class='card-body p-0'><div class='table-responsive'>");

        html.append("""
            <table class='table table-striped table-hover mb-0'>
                <thead class='table-light'>
                    <tr>
                        <th>ID</th>
                        <th>Código</th>
                        <th>Título</th>
                        <th>Categoría</th>
                        <th>Duración</th>
                        <th>Año</th>
                        <th>Precio</th>
                        <th>Premium</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
        """);

        html.append("<tbody>");

        for (Contenido c : contenidos) {

            String premiumBadge = c.isExclusivoPremium()
                    ? "<span class='badge bg-warning text-dark'>Premium</span>"
                    : "<span class='badge bg-secondary'>No</span>";

            html.append("<tr>");
            html.append("<td>").append(c.getId()).append("</td>");
            html.append("<td>").append(c.getCodigo()).append("</td>");
            html.append("<td>").append(c.getTitulo()).append("</td>");
            html.append("<td>").append(c.getCategoria()).append("</td>");
            html.append("<td>").append(c.getDuracion()).append("</td>");
            html.append("<td>").append(c.getAnio()).append("</td>");
            html.append("<td>$ ").append(c.getPrecio()).append("</td>");
            html.append("<td>").append(premiumBadge).append("</td>");

            html.append("<td>");
            html.append("<a href='/admin/contenidos/editar/").append(c.getId())
                    .append("' class='btn btn-sm btn-warning me-2'>✏️ Editar</a>");
            html.append("<a href='/admin/contenidos/eliminar/").append(c.getId())
                    .append("' class='btn btn-sm btn-danger' onclick='return confirm(\"¿Seguro?\");'>❌ Eliminar</a>");
            html.append("</td>");

            html.append("</tr>");
        }

        html.append("</tbody></table></div></div></div></div>");
        html.append(getHtmlFooter());

        return html.toString();
    }

    @GetMapping({"/nuevo", "/editar/{id}"})
    @ResponseBody
    public String mostrarFormulario(@PathVariable(required = false) Long id) {

        Contenido contenido;

        try {
            contenido = (id != null)
                    ? contenidoService.obtenerPorId(id)
                    : new Contenido();
        } catch (NotFoundException e) {
            return "No existe: " + e.getMessage();
        }

        String titulo = (id != null)
                ? "Editar Contenido (ID: " + id + ")"
                : "Nuevo Contenido";

        StringBuilder html = new StringBuilder();
        html.append(getHtmlHeader(titulo,
                ".header-link[href='/admin/contenidos']{font-weight:bold;}"));
        html.append(getNavbarHtml("Contenidos"));

        html.append("<div class='container mt-4'>");
        html.append("<h1 class='mb-4 text-success'>").append(titulo).append("</h1>");

        html.append("<div class='card shadow'><div class='card-body'>");

        html.append("<form action='/admin/contenidos/guardar' method='post'>");

        html.append("<input type='hidden' name='id' value='")
                .append(contenido.getId() != null ? contenido.getId() : "")
                .append("'>");

        html.append(createInputField("text", "codigo", "Código", contenido.getCodigo(), true));
        html.append(createInputField("text", "titulo", "Título", contenido.getTitulo(), true));
        html.append(createInputField("text", "descripcion", "Descripción", contenido.getDescripcion(), true));
        html.append(createInputField("text", "categoria", "Categoría", contenido.getCategoria(), true));
        html.append(createInputField("number", "duracion", "Duración", contenido.getDuracion() + "", true));
        html.append(createInputField("number", "anio", "Año", contenido.getAnio() + "", true));
        html.append(createInputField("number", "precio", "Precio", contenido.getPrecio() + "", true));

        html.append("""
            <div class='mb-3'>
                <label class='form-label' for='exclusivoPremium'>¿Exclusivo Premium?</label>
                <select class='form-select' name='exclusivoPremium'>
                    <option value='false' %s>No</option>
                    <option value='true' %s>Sí</option>
                </select>
            </div>
        """.formatted(
                !contenido.isExclusivoPremium() ? "selected" : "",
                contenido.isExclusivoPremium() ? "selected" : ""
        ));

        html.append("<button class='btn btn-success me-2'>Guardar</button>");
        html.append("<a href='/admin/contenidos' class='btn btn-secondary'>Cancelar</a>");

        html.append("</form></div></div></div>");
        html.append(getHtmlFooter());

        return html.toString();
    }

    @PostMapping("/guardar")
    public String guardarContenido(
            @RequestParam(required = false) Long id,
            @RequestParam String codigo,
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam String categoria,
            @RequestParam int duracion,
            @RequestParam int anio,
            @RequestParam double precio,
            @RequestParam boolean exclusivoPremium,
            RedirectAttributes ra) {

        try {

            Contenido c = new Contenido();
            c.setCodigo(codigo);
            c.setTitulo(titulo);
            c.setDescripcion(descripcion);
            c.setCategoria(categoria);
            c.setDuracion(duracion);
            c.setAnio(anio);
            c.setPrecio(precio);
            c.setExclusivoPremium(exclusivoPremium);

            if (id != null) {
                contenidoService.actualizarContenido(id, c);
                ra.addFlashAttribute("mensaje", "Contenido actualizado correctamente.");
            } else {
                contenidoService.crearContenido(c);
                ra.addFlashAttribute("mensaje", "Contenido creado correctamente.");
            }

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/admin/contenidos";
    }

    @GetMapping("/eliminar/{id}")
public String eliminarContenido(@PathVariable Long id, RedirectAttributes ra) {

    try {
        contenidoService.eliminar(id);
        ra.addFlashAttribute("mensaje", "Contenido con ID " + id + " eliminado correctamente.");
    } catch (NotFoundException e) {
        ra.addFlashAttribute("error", e.getMessage());
    } catch (Exception e) {
        ra.addFlashAttribute("error", "Error al eliminar el contenido: " + e.getMessage());
    }

    return "redirect:/admin/contenidos";
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
