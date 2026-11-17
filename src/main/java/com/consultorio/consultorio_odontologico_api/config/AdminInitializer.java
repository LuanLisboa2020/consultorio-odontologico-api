
package com.consultorio.consultorio_odontologico_api.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.consultorio.consultorio_odontologico_api.entity.Usuario;
import com.consultorio.consultorio_odontologico_api.enums.PerfilUsuario;
import com.consultorio.consultorio_odontologico_api.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "app.admin.bootstrap-enabled",
    havingValue = "true"
)
public class AdminInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminBootstrapProperties adminProperties;

    @Override
    @Transactional
    public void run(String... args) {

        if (adminProperties.getAdmins().isEmpty()) {
            throw new IllegalStateException("Nenhum administrador foi informado para o bootstrap.");
        }

        for (AdminBootstrapProperties.AdminCredential admin : adminProperties.getAdmins()) {

            criarAdministrador(admin);
        }
    }

    private void criarAdministrador(AdminBootstrapProperties.AdminCredential dados) {

        if (dados.getUsuario() == null || dados.getUsuario().isBlank()
                || dados.getEmail() == null || dados.getEmail().isBlank()
                || dados.getSenha() == null || dados.getSenha().isBlank()) {

            throw new IllegalStateException("Os dados do administrador não podem estar vazios.");
        }

        if (usuarioRepository.findByUsuario(dados.getUsuario()).isPresent()) {
            throw new IllegalStateException("O usuário informado já está cadastrado.");
        }

        if (usuarioRepository.findByEmail(dados.getEmail()).isPresent()) {
            throw new IllegalStateException("O e-mail informado já está cadastrado.");
        }

        Usuario admin = new Usuario();

        admin.setUsuario(dados.getUsuario());
        admin.setEmail(dados.getEmail());
        admin.setSenha(passwordEncoder.encode(dados.getSenha()));
        admin.setAtivo(true);
        admin.setPerfil(PerfilUsuario.ADMIN);

        usuarioRepository.save(admin);
    }
}
