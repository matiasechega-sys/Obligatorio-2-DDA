package com.example.streaming.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String homeHtml() {
        return """
            <!DOCTYPE html>
            <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>API Backend | Consola de Streaming</title>
                    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
                    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
                    
                    <style>
                        /* Fondo Suave y Limpio */
                        body { 
                            background-color: #f0f2f5; /* Gris muy claro */
                            color: #333; 
                            font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            min-height: 100vh;
                        }
                        /* Tarjeta principal - Ligera y Flotante */
                        .card { 
                            border-radius: 1.5rem; 
                            background-color: #ffffff; /* Blanco puro */
                            border: 1px solid #e0e0e0;
                            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
                        }
                        /* Título de la Plataforma */
                        .card-title {
                            color: #007bff; /* Azul de acento vibrante */
                            font-weight: 800;
                            letter-spacing: 0.5px;
                        }
                        /* Lista de Endpoints - Líneas de Contraste */
                        .list-group-item { 
                            transition: all 0.2s ease-in-out; 
                            border: none;
                            border-radius: 0.75rem;
                            margin-bottom: 8px;
                            background-color: #ffffff; 
                            padding: 15px 20px;
                            border-left: 4px solid transparent; /* Para el efecto hover */
                        }
                        .list-group-item:hover { 
                            transform: translateY(-2px); 
                            background-color: #e9f0f8; /* Fondo sutil al pasar el ratón */
                            border-left: 4px solid #007bff; /* Borde azul de enfoque */
                            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
                            cursor: pointer;
                        }
                        /* Estilo para los elementos del ABM (Administración) */
                        .list-group-item.admin-link:hover {
                            border-left: 4px solid #ffc107; /* Amarillo de advertencia/administración */
                            background-color: #fff9e6;
                        }

                        /* Enlaces de la lista */
                        .list-group-item a {
                            color: #333;
                            font-weight: 600;
                            text-decoration: none;
                        }
                        /* Insignias de Ruta */
                        .badge-route {
                            background-color: #007bff; /* Azul sólido */
                            color: white;
                            padding: 0.4em 0.8em;
                            border-radius: 50px;
                            font-size: 0.8rem;
                            font-weight: bold;
                        }
                        .badge-report {
                            background-color: #28a745; /* Verde para reportes */
                        }
                        .badge-admin {
                            background-color: #ffc107; /* Amarillo para administración */
                            color: #333;
                        }
                        
                        /* --- MODIFICACIÓN DE TÍTULOS --- */
                        .section-title {
                            /* Color más oscuro y contrastante */
                            color: #007bff; 
                            /* Más grande */
                            font-size: 1.5rem; 
                            /* Más peso */
                            font-weight: 700; 
                            /* Eliminamos el borde inferior que lo hacía ver débil */
                            border-bottom: none; 
                            padding-bottom: 5px;
                            margin-top: 35px;
                            margin-bottom: 15px;
                        }
                        /* --------------------------------- */
                        
                        .text-muted {
                            color: #6c757d !important;
                        }
                        .text-small {
                            font-size: 0.9rem;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="row justify-content-center">
                            <div class="col-md-8 col-lg-7">
                                <div class="card shadow-lg p-3">
                                    <div class="card-body p-5">
                                        <h1 class="card-title text-center mb-4">
                                            <i class="bi bi-display-fill me-2"></i> GESTIÓN DE STREAMING API
                                        </h1>
                                        
                                        <p class="text-center text-muted mb-5">
                                            **Consola de Administración Backend**. Utiliza los endpoints a continuación para la interacción y gestión de datos.
                                        </p>

                                        <h3 class="section-title text-warning">
                                            <i class="bi bi-gear-fill me-2"></i> Administración Manual (Vistas HTML)
                                        </h3>
                                        <ul class="list-group list-group-flush mb-4">
                                            <li class="list-group-item admin-link">
                                                <a href="/admin/usuarios" class="text-decoration-none d-flex justify-content-between align-items-center">
                                                    🛠️ **ABM de Usuarios**
                                                    <span class="badge badge-route badge-admin">/admin/usuarios</span>
                                                </a>
                                            </li>
                                            
                                            <li class="list-group-item admin-link">
                                                <a href="/admin/contenidos" class="text-decoration-none d-flex justify-content-between align-items-center">
                                                    🎬 **ABM de Contenidos**
                                                    <span class="badge badge-route badge-admin">/admin/contenidos</span>
                                                </a>
                                            </li>
                                            <li class="list-group-item admin-link">
                                                <a href="/admin/reproducciones" class="text-decoration-none d-flex justify-content-between align-items-center">
                                                    ▶ **ABM de Reproducciones**
                                                    <span class="badge badge-route badge-admin">/admin/reproducciones</span>
                                                </a>
                                            </li>
                                            </ul>
                                        
                                        <h3 class="section-title">
                                            <i class="bi bi-code-square me-2"></i> Endpoints Principales (CRUD REST)
                                        </h3>
                                        <ul class="list-group list-group-flush mb-4">
                                            <li class="list-group-item">
                                                <a href="/usuarios" class="text-decoration-none d-flex justify-content-between align-items-center">
                                                    👤 **Usuarios** (REST: JSON)
                                                    <span class="badge badge-route">/usuarios</span>
                                                </a>
                                            </li>
                                            <li class="list-group-item">
                                                <a href="/contenidos" class="text-decoration-none d-flex justify-content-between align-items-center">
                                                    🎬 **Contenidos** (Películas & Series)
                                                    <span class="badge badge-route">/contenidos</span>
                                                </a>
                                            </li>
                                            <li class="list-group-item">
                                                <a href="/reproducciones" class="text-decoration-none d-flex justify-content-between align-items-center">
                                                    ▶ **Reproducciones** (Historial y Avance)
                                                    <span class="badge badge-route">/reproducciones</span>
                                                </a>
                                            </li>
                                        </ul>

                                        <h3 class="section-title">
                                            <i class="bi bi-bar-chart-line-fill me-2"></i> Datos y Estadísticas
                                        </h3>
                                        <ul class="list-group list-group-flush">
                                            <li class="list-group-item">
                                                <a href="/consultas" class="text-decoration-none d-flex justify-content-between align-items-center">
                                                    📊 **Consultas y Reportes**
                                                    <span class="badge badge-route badge-report">/consultas</span>
                                                </a>
                                            </li>
                                        </ul>
                                        
                                        <p class="text-center mt-5 pt-3 border-top text-small text-muted">
                                            **Nota:** Los endpoints CRUD y de Consultas devuelven JSON y están destinados a clientes REST. El ABM devuelve HTML.
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
                </body>
            </html>
            """;
    }
}