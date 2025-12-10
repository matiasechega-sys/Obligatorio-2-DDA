package com.example.streaming.service;

import com.example.streaming.exception.NotFoundException;
import com.example.streaming.model.Contenido;
import com.example.streaming.model.Usuario;
import com.example.streaming.model.TipoUsuario;
import com.example.streaming.repository.ContenidoRepository;
import com.example.streaming.exception.AccesoDenegadoException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContenidoService {

    private final ContenidoRepository contenidoRepository;
    private final UsuarioService usuarioService;

    public ContenidoService(ContenidoRepository contenidoRepository, UsuarioService usuarioService) {
        this.contenidoRepository = contenidoRepository;
        this.usuarioService = usuarioService;
    }

    // --- C R E A R (POST) ---
    public Contenido crearContenido(Contenido contenido) {

        if (contenidoRepository.existsByCodigo(contenido.getCodigo())) {
            throw new RuntimeException("El código '" + contenido.getCodigo() + "' ya está registrado");
        }

        return contenidoRepository.save(contenido);
    }

    // --- L I S T A R (GET) ---
    public List<Contenido> listarContenidos() {
        return contenidoRepository.findAll();
    }

    // --- O B T E N E R P O R I D (GET por ID) ---
    public Contenido obtenerPorId(Long id) {
        return contenidoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contenido con ID " + id + " no encontrado"));
    }

    // --- A C T U A L I Z A R (PUT) ---
    public Contenido actualizarContenido(Long id, Contenido detalles) {

        Contenido existente = contenidoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contenido con ID " + id + " no encontrado para actualizar"));

        // Validación de código único al editar
        if (!existente.getCodigo().equals(detalles.getCodigo()) &&
                contenidoRepository.existsByCodigo(detalles.getCodigo())) {
            throw new RuntimeException("El código '" + detalles.getCodigo() + "' ya está registrado");
        }

        // Actualización de propiedades
        existente.setCodigo(detalles.getCodigo());
        existente.setTitulo(detalles.getTitulo());
        existente.setDescripcion(detalles.getDescripcion());
        existente.setCategoria(detalles.getCategoria());
        existente.setDuracion(detalles.getDuracion());
        existente.setAnio(detalles.getAnio());
        existente.setPrecio(detalles.getPrecio());
        existente.setExclusivoPremium(detalles.isExclusivoPremium());

        return contenidoRepository.save(existente);
    }

    // --- E L I M I N A R (DELETE) ---
    public void eliminar(Long id) {
        if (!contenidoRepository.existsById(id)) {
            throw new NotFoundException("Contenido con ID " + id + " no encontrado para eliminar");
        }
        contenidoRepository.deleteById(id);
    }

    public void validarAcceso(Long usuarioId, Long contenidoId) {

        // 1. Obtener Entidades (usando los métodos de servicio para manejar NotFoundException)
        Usuario usuario = usuarioService.obtener(usuarioId);
        Contenido contenido = obtenerPorId(contenidoId);

        // 2. Aplicar la Regla: Si es exclusivo, el usuario debe ser Premium
        if (contenido.isExclusivoPremium()) {

            // Verificamos si NO es premium (es decir, es TipoUsuario.ESTANDAR)
            if (usuario.getTipo() != TipoUsuario.PREMIUM) {

                // 3. Denegar acceso
                throw new AccesoDenegadoException(
                    "Acceso denegado. El contenido '" + contenido.getTitulo() + 
                    "' es exclusivo para miembros Premium."
                );
            }
        }
    }
}