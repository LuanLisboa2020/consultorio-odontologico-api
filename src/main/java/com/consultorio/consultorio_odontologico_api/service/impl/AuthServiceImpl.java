
package com.consultorio.consultorio_odontologico_api.service.impl;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.consultorio.consultorio_odontologico_api.dto.request.LoginRequest;
import com.consultorio.consultorio_odontologico_api.dto.response.LoginResponse;
import com.consultorio.consultorio_odontologico_api.entity.Usuario;
import com.consultorio.consultorio_odontologico_api.security.JwtService;
import com.consultorio.consultorio_odontologico_api.service.AuthService;
import com.consultorio.consultorio_odontologico_api.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {

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

        String accessToken = jwtService.gerarAccessToken(usuario.getId());

        return new LoginResponse(
                accessToken,
                "Bearer",
                jwtService.getAccessTokenExpirationSeconds()
        );
    }
}
