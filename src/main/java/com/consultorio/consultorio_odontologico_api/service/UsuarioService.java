package com.consultorio.consultorio_odontologico_api.service;

import java.util.Optional;
import java.util.UUID;

import com.consultorio.consultorio_odontologico_api.entity.Usuario;

public interface UsuarioService {

    Usuario salvar(Usuario usuario);

    Optional<Usuario> buscarPorId(UUID id);

    Optional<Usuario> buscarPorUsuario(String usuario);

    Optional<Usuario> buscarPorEmail(String email);
}