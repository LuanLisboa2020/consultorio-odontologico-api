
package com.consultorio.consultorio_odontologico_api.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.admin")
public class AdminBootstrapProperties {

    private boolean bootstrapEnabled = false;

    private List<AdminCredential> admins = new ArrayList<>();

    @Getter
    @Setter
    public static class AdminCredential {

        private String usuario;

        private String email;

        private String senha;
    }
}
