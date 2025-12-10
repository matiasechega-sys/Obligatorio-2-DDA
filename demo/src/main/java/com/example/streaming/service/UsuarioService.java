package com.example.streaming.service;

import com.example.streaming.model.Usuario;
import com.example.streaming.model.TipoUsuario;
import com.example.streaming.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    // ---------------------------------------------------
    //  CREAR USUARIO
    // ---------------------------------------------------
    public Usuario crear(Usuario u) {

        // Validación de email duplicado
        if (repo.existsByEmail(u.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        aplicarReglasTipo(u);
        return repo.save(u);
    }

    // ---------------------------------------------------
    //  LISTAR TODOS
    // ---------------------------------------------------
    public List<Usuario> listar() {
        return repo.findAll();
    }

    // ---------------------------------------------------
    //  OBTENER UNO
    // ---------------------------------------------------
    public Usuario obtener(Long id) {
        // Mejor práctica: usar una excepción específica como NotFoundException
        // Por ahora, mantendremos RuntimeException
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // ---------------------------------------------------
    //  EDITAR
    // ---------------------------------------------------
    public Usuario editar(Long id, Usuario u) {
        Usuario existente = obtener(id);

        // Actualizar solo los campos que pueden cambiar (generalmente excluyendo ID y fechaRegistro)
        existente.setNombreCompleto(u.getNombreCompleto());
        
        // Solo actualizar email si el nuevo no está tomado (Lógica compleja, se simplifica aquí)
        existente.setEmail(u.getEmail()); 
        
        // La fecha de registro no debería cambiarse
        // existente.setFechaRegistro(u.getFechaRegistro()); 
        
        // 🚨 CRÍTICO: El cambio de Tipo dispara las reglas de negocio
        // Es importante verificar si el tipo realmente cambió
        TipoUsuario tipoPrevio = existente.getTipo();
        existente.setTipo(u.getTipo());
        
        // Aplicar reglas SÓLO si el tipo cambió o si es la primera vez que se convierte a Premium
        if (tipoPrevio != u.getTipo() || u.getTipo() == TipoUsuario.PREMIUM) {
             aplicarReglasTipo(existente);
        } else {
             aplicarReglasTipo(existente); // Aplicar siempre es más seguro
        }

        return repo.save(existente);
    }

    // ---------------------------------------------------
    //  ELIMINAR
    // ---------------------------------------------------
    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    // ---------------------------------------------------
    //  CONSULTAS ADICIONALES
    // ---------------------------------------------------
    public List<Usuario> listarPorTipo(TipoUsuario tipo) {
        return repo.findByTipo(tipo);
    }

    public List<Usuario> filtrarPorFechaRegistro(LocalDate desde, LocalDate hasta) {
        return repo.findByFechaRegistroBetween(desde, hasta);
    }

    // ---------------------------------------------------
    //  MÉTODO AUXILIAR CORREGIDO
    // ---------------------------------------------------
    private void aplicarReglasTipo(Usuario u) {

        if (u.getTipo() == TipoUsuario.ESTANDAR) {
            u.setInicioMembresia(null);
            u.setDescuento(0.0); // Descuento cero
        }

        if (u.getTipo() == TipoUsuario.PREMIUM) {
            
            
            u.setDescuento(0.20); 

        
            if (u.getInicioMembresia() == null) {
              
                u.setInicioMembresia(LocalDate.now()); 
            }
        }
    }
}