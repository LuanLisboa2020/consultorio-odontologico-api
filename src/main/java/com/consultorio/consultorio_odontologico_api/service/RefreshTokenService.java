package com.consultorio.consultorio_odontologico_api.service;

import java.util.Optional;
import java.util.UUID;

import com.consultorio.consultorio_odontologico_api.entity.RefreshToken;
import com.consultorio.consultorio_odontologico_api.entity.Usuario;

public interface RefreshTokenService {

    String criarRefreshToken(Usuario usuario, UUID sessionId);

    Optional<RefreshToken> buscarPorToken(String token);

    void revogarToken(RefreshToken refreshToken);

    void revogarSessao(UUID usuarioId, UUID sessionId);

}
