package com.clinica.service;

import com.clinica.model.Secretaria;
import com.clinica.repository.RepoSecretaria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SecreService {

    private final AuthenticationManager authManager;
    private final JWTService jwtService;
    private final RepoSecretaria repoSecretaria;
    private final BCryptPasswordEncoder encoder;
    private final UserDetailsService userDetailsService;

    public SecreService(AuthenticationManager authManager, JWTService jwtService, RepoSecretaria repoSecretaria, UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        encoder = new BCryptPasswordEncoder(12);
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.repoSecretaria = repoSecretaria;
    }

    public Secretaria registrar(Secretaria secretaria){
        secretaria.setContrasena(encoder.encode(secretaria.getContrasena()));
        return repoSecretaria.save(secretaria);
    }

    public Map<String, String> acceder(Secretaria secretaria) {
        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken
                        (secretaria.getCorreo(), secretaria.getContrasena()));

        if(authentication.isAuthenticated()){
            String accessToken = jwtService.generateToken(secretaria.getCorreo());
            String refreshToken = jwtService.generateRefreshToken(secretaria.getCorreo());
            return Map.of("accessToken", accessToken,
                    "refreshToken", refreshToken);
        }
        return Map.of("error", "failed");
    }


    public String refreshAccessToken(String refreshToken) {
        String correo = jwtService.extractCorreo(refreshToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(correo);

        if (!jwtService.validateToken(refreshToken, userDetails)) {
            throw new RuntimeException("Refresh token inválido o expirado");
        }

        return jwtService.generateToken(correo);
    }

}
