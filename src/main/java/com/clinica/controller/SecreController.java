package com.clinica.controller;

import com.clinica.model.Secretaria;
import com.clinica.model.SecretariaPrincipal;
import com.clinica.service.JWTService;
import com.clinica.service.SecreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class SecreController {

    private final SecreService secreService;

    public SecreController(SecreService secreService) {
        this.secreService = secreService;
    }

    @PostMapping("/registrar")
    public Secretaria registrarSecretaria(@RequestBody Secretaria secretaria){
        return secreService.registrar(secretaria);
    }

    @PostMapping("/acceder")
    public Map<String, String> login(@RequestBody Secretaria secretaria){
        return secreService.acceder(secretaria);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        try {
            String newAccessToken = secreService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }


}
