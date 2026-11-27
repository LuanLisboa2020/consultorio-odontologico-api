package com.consultorio.consultorio_odontologico_api.security;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.consultorio.consultorio_odontologico_api.entity.Usuario;
import com.consultorio.consultorio_odontologico_api.service.UsuarioService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                UUID usuarioId = jwtService.extrairUsuarioId(token);

                usuarioService.buscarPorId(usuarioId)
                        .filter(Usuario::isAtivo)
                        .ifPresent(usuario -> autenticarUsuario(usuario, request));

            } catch (JwtException | IllegalArgumentException e) {
                // Token inválido: a requisição continuará sem autenticação.
            }
        }

        filterChain.doFilter(request, response);
    }

    private void autenticarUsuario(
            Usuario usuario,
            HttpServletRequest request) {

        UserDetails userDetails = User.withUsername(usuario.getUsuario())
                .password(usuario.getSenha())
                .authorities("ROLE_" + usuario.getPerfil().name())
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
