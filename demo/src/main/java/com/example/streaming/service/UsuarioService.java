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

    public Usuario crear(Usuario u) {

        // Validación de email duplicado
        if (repo.existsByEmail(u.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        aplicarReglasTipo(u);
        return repo.save(u);
    }

    public List<Usuario> listar() {
        return repo.findAll();
    }

    public Usuario obtener(Long id) {
       
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario editar(Long id, Usuario u) {
        Usuario existente = obtener(id);

        existente.setNombreCompleto(u.getNombreCompleto());
     
        existente.setEmail(u.getEmail()); 
        
        TipoUsuario tipoPrevio = existente.getTipo();
        existente.setTipo(u.getTipo());
        
        
        if (tipoPrevio != u.getTipo() || u.getTipo() == TipoUsuario.PREMIUM) {
             aplicarReglasTipo(existente);
        } else {
             aplicarReglasTipo(existente); 
        }

        return repo.save(existente);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    public List<Usuario> listarPorTipo(TipoUsuario tipo) {
        return repo.findByTipo(tipo);
    }

    public List<Usuario> filtrarPorFechaRegistro(LocalDate desde, LocalDate hasta) {
        return repo.findByFechaRegistroBetween(desde, hasta);
    }

    private void aplicarReglasTipo(Usuario u) {

        if (u.getTipo() == TipoUsuario.ESTANDAR) {
            u.setInicioMembresia(null);
            u.setDescuento(0.0); 
        }

        if (u.getTipo() == TipoUsuario.PREMIUM) {
            
            
            u.setDescuento(0.20); 

        
            if (u.getInicioMembresia() == null) {
              
                u.setInicioMembresia(LocalDate.now()); 
            }
        }
    }
}