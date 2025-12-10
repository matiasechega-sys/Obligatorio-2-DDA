package com.example.streaming.controller;

import com.example.streaming.exception.AccesoDenegadoException;
import com.example.streaming.exception.NotFoundException;
import com.example.streaming.model.Reproduccion;
import com.example.streaming.model.Usuario;
import com.example.streaming.model.Contenido;
import com.example.streaming.service.ReproduccionService;
import com.example.streaming.service.UsuarioService;
import com.example.streaming.service.ContenidoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/reproducciones")
public class ReproduccionWebController {

    @Autowired private ReproduccionService reproduccionService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private ContenidoService contenidoService;

    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping(produces = "text/html")
    @ResponseBody
    public String listarReproducciones(Model model) {

        List<Reproduccion> reproducciones = reproduccionService.listarTodos();

        Map<String, Object> reporteMasVisto = obtenerContenidoMasReproducidoReal(reproducciones);

        StringBuilder html = new StringBuilder();

        html.append(getHtmlHeader("Gestion de Reproducciones",
                ".header-link[href='/admin/reproducciones'] { font-weight: bold; }"));
        html.append(getNavbarHtml("Reproducciones"));

        html.append("<div class='container mt-4'>")
            .append("<h1 class='mb-4 text-primary'>Gestion de Reproducciones (ABM)</h1>");

        Optional.ofNullable(model.getAttribute("mensaje")).ifPresent(msg -> html.append(
            "<div class='alert alert-success alert-dismissible fade show' role='alert'>"
            + msg
            + "<button type='button' class='btn-close' data-bs-dismiss='alert'></button>"
            + "</div>"
        ));

        Optional.ofNullable(model.getAttribute("error")).ifPresent(err -> html.append(
            "<div class='alert alert-danger alert-dismissible fade show' role='alert'>"
            + err
            + "<button type='button' class='btn-close' data-bs-dismiss='alert'></button>"
            + "</div>"
        ));

        html.append(getReporteMasVistoHtml(reporteMasVisto));

        html.append(
            "<div class='d-flex justify-content-end mb-3'>"
            + "<a href='/admin/reproducciones/nuevo' class='btn btn-success shadow-sm'>"
            + "Registrar Nueva Reproduccion"
            + "</a></div>"
        );

        html.append("<div class='card shadow'>")
            .append("<div class='card-header bg-primary text-white'>")
            .append("<h4 class='mb-0'>Listado de Registros (Total: ")
            .append(reproducciones.size())
            .append(")</h4></div>")
            .append("<div class='card-body p-0'><div class='table-responsive'>");

        html.append(
            "<table class='table table-striped table-hover mb-0'>"
            + "<thead class='table-light'>"
            + "<tr>"
            + "<th>ID</th>"
            + "<th>Usuario</th>"
            + "<th>Contenido</th>"
            + "<th>Duracion (seg)</th>"
            + "<th>Calificacion</th>"
            + "<th>Fecha y Hora</th>"
            + "<th>Acciones</th>"
            + "</tr>"
            + "</thead><tbody>"
        );

        for (Reproduccion r : reproducciones) {

            String fechaStr = Optional.ofNullable(r.getFechaHora())
                    .map(DATETIME_FORMATTER::format)
                    .orElse("-");

            String calificacionBadge = r.getCalificacion() > 0
                    ? "<span class='badge bg-warning text-dark'>" + r.getCalificacion() + " / 5</span>"
                    : "<span class='badge bg-secondary'>N/A</span>";

            html.append("<tr>")
                .append("<td>").append(r.getId()).append("</td>")
                .append("<td>").append(r.getUsuario() != null ? r.getUsuario().getNombreCompleto() : "N/A").append("</td>")
                .append("<td>").append(r.getContenido() != null ? r.getContenido().getTitulo() : "N/A").append("</td>")
                .append("<td>").append(r.getDuracionReproducida()).append("</td>")
                .append("<td>").append(calificacionBadge).append("</td>")
                .append("<td>").append(fechaStr).append("</td>")
                .append("<td>")
                .append("<a href='/admin/reproducciones/editar/").append(r.getId())
                .append("' class='btn btn-sm btn-warning me-2'>Editar</a>")
                .append("<a href='/admin/reproducciones/eliminar/").append(r.getId())
                .append("' class='btn btn-sm btn-danger' onclick='return confirm(\"Seguro?\");'>Eliminar</a>")
                .append("</td>")
                .append("</tr>");
        }

        html.append("</tbody></table></div></div></div></div>");
        html.append(getHtmlFooter());

        return html.toString();
    }

    @GetMapping({"/nuevo", "/editar/{id}"})
    @ResponseBody
    public String mostrarFormulario(@PathVariable(required = false) Long id) {

        Reproduccion r;
        try {
            r = (id != null) ? reproduccionService.obtenerPorId(id) : new Reproduccion();
        } catch (NotFoundException e) {
            return "redirect:/admin/reproducciones";
        }

        String titulo = id != null ? "Editar Registro (ID: " + id + ")" : "Nuevo Registro de Reproduccion";

        List<Usuario> usuarios = usuarioService.listar();
        List<Contenido> contenidos = contenidoService.listarContenidos();

        StringBuilder html = new StringBuilder();
        html.append(getHtmlHeader(titulo,
                ".header-link[href='/admin/reproducciones']{font-weight:bold;}"));
        html.append(getNavbarHtml("Reproducciones"));

        html.append("<div class='container mt-4'>")
            .append("<h1 class='mb-4 text-success'>").append(titulo).append("</h1>")
            .append("<div class='card shadow'><div class='card-body'>")
            .append("<form action='/admin/reproducciones/guardar' method='post'>");

        html.append("<input type='hidden' name='id' value='")
            .append(r.getId() != null ? r.getId() : "")
            .append("'>");

        html.append("<div class='mb-3'><label class='form-label'>Usuario</label>")
            .append("<select name='usuarioId' class='form-select' required>")
            .append("<option value=''>-- Seleccione --</option>");

        Long selectedUser = r.getUsuario() != null ? r.getUsuario().getId() : null;
        for (Usuario u : usuarios) {
            html.append("<option value='").append(u.getId()).append("' ")
                .append(selectedUser != null && selectedUser.equals(u.getId()) ? "selected" : "")
                .append(">").append(u.getNombreCompleto()).append("</option>");
        }
        html.append("</select></div>");

        html.append("<div class='mb-3'><label class='form-label'>Contenido</label>")
            .append("<select name='contenidoId' class='form-select' required>")
            .append("<option value=''>-- Seleccione --</option>");

        Long selectedContent = r.getContenido() != null ? r.getContenido().getId() : null;
        for (Contenido c : contenidos) {
            html.append("<option value='").append(c.getId()).append("' ")
                .append(selectedContent != null && selectedContent.equals(c.getId()) ? "selected" : "")
                .append(">").append(c.getTitulo()).append("</option>");
        }
        html.append("</select></div>");

        html.append(createInputField("number", "duracionReproducida",
                "Duracion Reproducida (segundos)",
                r.getDuracionReproducida() > 0 ? "" + r.getDuracionReproducida() : "", true));

        html.append(createInputField("number", "calificacion", "Calificacion (1-5)",
                r.getCalificacion() > 0 ? "" + r.getCalificacion() : "", false));

        String fechaManual = (r.getFechaHora() != null)
                ? r.getFechaHora().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                : "";

        html.append(createInputField("datetime-local", "fechaHoraManual", "Fecha y Hora (Opcional)",
                fechaManual, false));

        html.append(
            "<button class='btn btn-success me-2'>Guardar</button>"
            + "<a href='/admin/reproducciones' class='btn btn-secondary'>Cancelar</a>"
        );

        html.append("</form></div></div></div>");
        html.append(getHtmlFooter());

        return html.toString();
    }

    @PostMapping("/guardar")
    public String guardarReproduccion(
            @RequestParam(required = false) Long id,
            @RequestParam Long usuarioId,
            @RequestParam Long contenidoId,
            @RequestParam int duracionReproducida,
            @RequestParam(required = false, defaultValue = "0") int calificacion,
            @RequestParam(required = false) String fechaHoraManual,
            RedirectAttributes ra) {

        try {

            LocalDateTime fechaParsed = null;

            if (fechaHoraManual != null && !fechaHoraManual.isBlank()) {
                fechaParsed = LocalDateTime.parse(fechaHoraManual);
            }

            if (id == null) {
                reproduccionService.registrarReproduccion(
                        usuarioId, contenidoId, duracionReproducida, calificacion
                );
                ra.addFlashAttribute("mensaje", "Reproduccion creada correctamente.");
            } else {

                Reproduccion cambios = new Reproduccion();

                Usuario u = new Usuario();
                u.setId(usuarioId);
                cambios.setUsuario(u);

                Contenido c = new Contenido();
                c.setId(contenidoId);
                cambios.setContenido(c);

                cambios.setDuracionReproducida(duracionReproducida);
                cambios.setCalificacion(calificacion);

                if (fechaParsed != null)
                    cambios.setFechaHora(fechaParsed);

                reproduccionService.actualizarReproduccion(id, cambios);
                ra.addFlashAttribute("mensaje", "Reproduccion modificada correctamente.");
            }

        } catch (NotFoundException e) {
            ra.addFlashAttribute("error", "Error de Entidad (ID invalido): " + e.getMessage());
        } catch (AccesoDenegadoException e) {
            ra.addFlashAttribute("error", "Regla de negocio: " + e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error general al guardar: " + e.getMessage());
        }

        return "redirect:/admin/reproducciones";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarReproduccion(@PathVariable Long id, RedirectAttributes ra) {
        try {
            reproduccionService.eliminar(id);
            ra.addFlashAttribute("mensaje", "Reproduccion eliminada.");
        } catch (NotFoundException e) {
            ra.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error general al eliminar: " + e.getMessage());
        }

        return "redirect:/admin/reproducciones";
    }

    private Map<String, Object> obtenerContenidoMasReproducidoReal(List<Reproduccion> reproducciones) {

        Map<String, Object> reporte = new HashMap<>();

        if (reproducciones == null || reproducciones.isEmpty()) {
            reporte.put("titulo", "N/A (No hay reproducciones registradas)");
            reporte.put("categoria", "N/A");
            reporte.put("count", 0);
            reporte.put("duracionTotal", 0);
            return reporte;
        }

        Map<Contenido, List<Reproduccion>> reproduccionesPorContenido =
                reproducciones.stream()
                        .filter(r -> r.getContenido() != null)
                        .collect(Collectors.groupingBy(Reproduccion::getContenido));

        if (reproduccionesPorContenido.isEmpty()) {
            reporte.put("titulo", "N/A (Reproducciones sin contenido valido)");
            reporte.put("categoria", "N/A");
            reporte.put("count", 0);
            reporte.put("duracionTotal", 0);
            return reporte;
        }

        Optional<Map.Entry<Contenido, List<Reproduccion>>> masVistoEntry =
                reproduccionesPorContenido.entrySet().stream()
                        .max(Comparator.comparingInt(entry -> entry.getValue().size()));

        if (masVistoEntry.isPresent()) {
            Contenido masVisto = masVistoEntry.get().getKey();
            List<Reproduccion> listaReproducciones = masVistoEntry.get().getValue();

            int count = listaReproducciones.size();
            int duracionTotalSeg = listaReproducciones.stream()
                    .mapToInt(Reproduccion::getDuracionReproducida)
                    .sum();

            reporte.put("titulo", masVisto.getTitulo());
            reporte.put("categoria", masVisto.getCategoria());
            reporte.put("count", count);
            reporte.put("duracionTotal", duracionTotalSeg);
        } else {
            reporte.put("titulo", "N/A (Error al procesar el reporte)");
            reporte.put("categoria", "N/A");
            reporte.put("duracionTotal", 0);
            reporte.put("count", 0);
        }

        return reporte;
    }

    private String getReporteMasVistoHtml(Map<String, Object> reporte) {

        String titulo = (String) reporte.get("titulo");
        String categoria = (String) reporte.get("categoria");
        int count = (int) reporte.get("count");
        int duracionTotalSeg = (int) reporte.get("duracionTotal");

        long minutosTotal = duracionTotalSeg / 60;
        long horasTotal = minutosTotal / 60;
        long minutosRestantes = minutosTotal % 60;

        String duracionTotalStr = String.format("%d hs %d min", horasTotal, minutosRestantes);

        return
            "<div class='card bg-info text-white shadow-lg mb-4'>"
            + "<div class='card-body d-flex justify-content-between align-items-center'>"
            + "<div>"
            + "<h5 class='card-title mb-1'>Contenido Mas Visto (Reporte Calculado)</h5>"
            + "<h2 class='card-text mb-0'>" + titulo + "</h2>"
            + "<span class='badge bg-light text-info'>" + categoria + "</span>"
            + "</div>"
            + "<div class='text-end'>"
            + "<p class='mb-0'>"
            + "<span style='font-size: 2.5rem; font-weight: bold;'>" + count + "</span>"
            + "<small class='d-block'>Reproducciones</small>"
            + "</p>"
            + "<small class='text-light'>Tiempo total visto: " + duracionTotalStr + "</small>"
            + "</div>"
            + "</div></div>";
    }

    private String getHtmlHeader(String title, String customStyle) {
        return
        "<!DOCTYPE html>"
        + "<html lang='es'><head>"
        + "<meta charset='UTF-8'>"
        + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
        + "<title>" + title + "</title>"
        + "<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet'>"
        + "<link href='https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css' rel='stylesheet'>"
        + "<style>body{background-color:#f8f9fa;} .header-link{font-weight:bold;} " + customStyle + "</style>"
        + "</head><body>";
    }

    private String getNavbarHtml(String active) {

        String usuariosClass = active.equals("Usuarios") ? "active header-link" : "header-link";
        String contenidosClass = active.equals("Contenidos") ? "active header-link" : "header-link";
        String reproduccionesClass = active.equals("Reproducciones") ? "active header-link" : "header-link";

        return
        "<nav class='navbar navbar-expand-lg navbar-dark bg-dark shadow-sm'>"
        + "<div class='container-fluid'>"
        + "<a class='navbar-brand' href='/'>Inicio API</a>"
        + "<div class='collapse navbar-collapse'>"
        + "<ul class='navbar-nav me-auto'>"
        + "<li class='nav-item'><a class='nav-link " + usuariosClass + "' href='/admin/usuarios'>Usuarios</a></li>"
        + "<li class='nav-item'><a class='nav-link " + contenidosClass + "' href='/admin/contenidos'>Contenidos</a></li>"
        + "<li class='nav-item'><a class='nav-link " + reproduccionesClass + "' href='/admin/reproducciones'>Reproducciones</a></li>"
        + "<li class='nav-item'><a class='nav-link header-link' href='/consultas'>Consultas</a></li>"
        + "</ul></div></div></nav>";
    }

    private String createInputField(String type, String name, String label, String value, boolean required) {

        String requiredAttr = required ? "required" : "";
        String extra = "";

        if ("number".equals(type)) {
            if (name.equals("calificacion")) extra = " min='1' max='5'";
            if (name.equals("duracionReproducida")) extra = " step='1'";
        }

        if ("datetime-local".equals(type)) {
            extra += " step='1'";
        }

        return
        "<div class='mb-3'>"
        + "<label class='form-label' for='" + name + "'>" + label + "</label>"
        + "<input type='" + type + "' id='" + name + "' name='" + name + "' class='form-control'"
        + " value='" + value + "' " + requiredAttr + extra + ">"
        + "</div>";
    }

    private String getHtmlFooter() {
        return
        "<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js'></script>"
        + "</body></html>";
    }
}
