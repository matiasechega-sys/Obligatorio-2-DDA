#  Plataforma de Streaming - Gestión de Contenido y Usuarios (API)

##  Descripción General del Proyecto

Este proyecto implementa un sistema de gestión de una plataforma de streaming, cubriendo la administración de usuarios (Estándar/Premium), contenidos (Series/Películas) y el registro de reproducciones y calificaciones.

El **Backend (Spring Boot)** maneja toda la lógica de negocio, incluyendo la **validación de acceso Premium** y la **aplicación automática del descuento del 20%** a los usuarios Premium. Además, el backend es responsable de **servir las vistas del Frontend** mediante plantillas.

---

##  Integrantes del Equipo

| Rol | Nombre Completo | Repositorio GitHub |
| --- | --- | --- |
| Desarrollador Backend | **Lucas Justet** | [https://github.com/LucasJustet] |
| Desarrollador Backend | **Matías Echegaray** | [https://github.com/matiasechega-sys] |

---

##  Stack Tecnológico

| Componente | Tecnología | Versión | Notas Relevantes |
| --- | --- | --- | --- |
| **Backend (Lógica/API)** | Spring Boot | 3.5.7 | Implementa la lógica (Services), Controllers REST y JPA. |
| **Base de Datos** | **MySQL** | [Versión 8.0 CE] | Base de datos relacional persistente. |
| **Frontend (Vistas)** | [Tecnología de Plantillas, ej: Thymeleaf] | [Versión] |El renderizado de vistas (SSR) es gestionado por todos los Controladores tanto Rest como Controlador web como por  (ej. UsuarioWebController o UsuarioController).

---

##  Ejecución del Programa

Sigue estos pasos para ejecutar el proyecto en tu entorno local.

### 1. Configuración y Ejecución del Backend

1.  **Clonar Repositorio:**
    ```bash
    git clone [[https://docs.github.com/es/repositories/creating-and-managing-repositories/quickstart-for-repositories](https://docs.github.com/es/repositories/creating-and-managing-repositories/quickstart-for-repositories)]
    cd nombre-del-proyecto-backend
    ```
2.  **Base de Datos:**
    * Asegúrate de que el servidor **MySQL** esté en ejecución.
    * Verifica las credenciales en `application.properties`.
    * Utiliza tu cliente SQL (Workbench, DBeaver, etc.) para ejecutar el script **`backup_data.sql`** y cargar los datos de prueba.
3.  **Compilar y Ejecutar:**
    ```bash
    ./mvnw spring-boot:run 
    # o ejecutar la clase principal com.example.streaming.StreamingApplication
    ```
    * La aplicación estará disponible en `http://localhost:5000` (o el puerto configurado).

### 2. Frontend (Server-Side Rendering)

 **Lógica de Renderizado:** Las vistas son servidas por los Controladores: la página de inicio es gestionada por el `HomeController`, y las vistas Endpoints de las entidades (listado, formularios, enlaces para cada metodo endpoint ) son servidas por sus respectivos **Controllers de Entidad** (ej., `UsuarioController`, `ContenidoController`,`ReproduccionController`), Las vistas CRUD (ABM) son servidas por los siguientes Controladores Web (retornando HTML):`UsuarioWebController`,`ContenidoWebController`,`ReproduccionWebController`
 **Acceso:** Abre tu navegador y navega a la URL base: `http://localhost:5000/`.
---

##  Reglas de Negocio Clave (Servicios)

La lógica crítica está encapsulada en la capa de servicios:

1.  **Regla de Descuento (UsuarioService):**
    * Al crear o editar un `Usuario` a tipo `PREMIUM`, el campo `descuento` se asigna automáticamente a **`0.20`** (20%). Para `ESTANDAR`, se asigna a `0.0`.
2.  **Validación de Acceso Premium (ReproduccionService):**
    * Se verifica que un usuario `ESTANDAR` no pueda registrar una reproducción en un `Contenido` que tenga `exclusivoPremium = true`. Esto resulta en un error **403 Forbidden**.
3.  **Fechas de Membresía:**
    * La `inicioMembresia` se establece con la fecha actual (`LocalDate.now()`) solo en el momento en que un usuario pasa de Estándar a Premium.

---

##  Endpoints de la API (CRUD Completo y Consultas)

Esta tabla detalla las rutas RESTful disponibles para interactuar con la aplicación.

| Recurso | Método | Ruta | Descripción |
| --- | --- | --- | --- |
| **Usuarios** | `POST` | `/usuarios` | Crea un nuevo usuario. **Aplica descuento automático** (20% Premium / 0% Estándar). |
| **Usuarios** | `GET` | `/usuarios/{id}` | Obtiene un usuario específico. |
| **Usuarios** | `PUT` | `/usuarios/{id}` | Edita un usuario. Dispara la lógica para revalidar el `descuento` y la `inicioMembresia`. |
| **Usuarios** | `DELETE` | `/usuarios/{id}` | Elimina un usuario del sistema. |
| **Contenido** | `POST` | `/contenidos` | **Crea un nuevo contenido** (película o serie). |
| **Contenido** | `GET` | `/contenidos/{id}` | Obtiene los detalles de un contenido por ID. |
| **Contenido** | `PUT` | `/contenidos/{id}` | **Actualiza los detalles de un contenido** existente. |
| **Contenido** | `DELETE` | `/contenidos/{id}` | **Elimina un contenido** del sistema. |
| **Reproducciones** | `POST` | `/reproducciones` | Registra una nueva reproducción. **Incluye validación Premium (403)**. |
| **Reproducciones** | `GET` | `/reproducciones/{id}` | Obtiene los detalles de una reproducción por ID. |
| **Reproducciones** | `PUT` | `/reproducciones/{id}` | Actualiza duración/calificación de una reproducción. |
| **Reproducciones** | `DELETE` | `/reproducciones/{id}` | Elimina el registro de una reproducción. |

---

##  Consultas de Filtrado de la API (Accesibles por GET)

Estas funcionalidades están implementadas en los controladores para permitir el filtrado de datos mediante peticiones `GET` (ej. vía Postman).

| ID | Objetivo de la Consulta | Ruta/Parámetros de Ejemplo | SQL/Lógica Implícita |
| --- | --- | --- | --- |
| **A** | **Contenidos más populares** (Contenidos con más de $N$ reproducciones) | `/contenidos/mas_vistos?n=3` | `GROUP BY contenido_id HAVING COUNT(*) > N` |
| **B** | **Contenidos Premium Exclusivos** | `/contenidos/exclusivos` | `ContenidoRepository.findByExclusivoPremium(true)`. |
| **C** | **Reproducciones de un Usuario** | `/reproducciones/usuario/{id}` | `ReproduccionRepository.findByUsuarioId(id)`. |
| **D** | **Usuarios Registrados en un Rango** | `/usuarios/filtrar?desde=2024-01-01&hasta=2024-06-01` | `UsuarioRepository.findByFechaRegistroBetween(inicio, fin)`. |
| **E** | **Reproducciones en una Fecha Específica** | `/reproducciones/filtrar?inicio=2025-03-07T00:00:00&fin=2025-03-07T23:59:59` | `ReproduccionRepository.findByFechaHoraBetween(inicio, fin)`. |