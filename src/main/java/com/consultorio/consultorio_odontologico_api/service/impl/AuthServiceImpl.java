package com.consultorio.consultorio_odontologico_api.service.impl;

import java.util.UUID;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.consultorio.consultorio_odontologico_api.dto.request.LoginRequest;
import com.consultorio.consultorio_odontologico_api.dto.response.AuthResult;
import com.consultorio.consultorio_odontologico_api.dto.response.LoginResponse;
import com.consultorio.consultorio_odontologico_api.entity.Usuario;
import com.consultorio.consultorio_odontologico_api.security.JwtService;
import com.consultorio.consultorio_odontologico_api.service.AuthService;
import com.consultorio.consultorio_odontologico_api.service.RefreshTokenCreationResult;
import com.consultorio.consultorio_odontologico_api.service.RefreshTokenService;
import com.consultorio.consultorio_odontologico_api.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public AuthResult login(LoginRequest request) {

        Usuario usuario = usuarioService
                .buscarPorUsuario(request.getUsuario())
                .orElseThrow(() -> new BadCredentialsException("Usuário ou senha inválidos."));

        if (!usuario.isAtivo()) {
            throw new BadCredentialsException("Usuário ou senha inválidos.");
        }

        if (!passwordEncoder.matches(
                request.getSenha(), usuario.getSenha())) {
            throw new BadCredentialsException("Usuário ou senha inválidos.");
        }

        // Cria um identificador exclusivo para esta sessão.
        UUID sessionId = UUID.randomUUID();

        // Gera o Access Token.
        String accessToken = jwtService.gerarAccessToken(usuario.getId());

        // Gera o Refresh Token e armazena somente seu hash no banco.
        RefreshTokenCreationResult refreshTokenResult = refreshTokenService.criarRefreshToken(usuario, sessionId);

        // Monta a resposta pública do login.
        LoginResponse loginResponse = new LoginResponse(
                accessToken,
                "Bearer",
                jwtService.getAccessTokenExpirationSeconds()
        );

        // Retorna os dados públicos e o Refresh Token separadamente.
        return new AuthResult(loginResponse, refreshTokenResult.token());
    }
}
