package com.clinica.controller;

import com.clinica.dto.psicologo.PsicologoRequest;
import com.clinica.dto.psicologo.PsicologoResponse;
import com.clinica.service.SecreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/secretaria/psicologos")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('SECRETARIA')")
public class PsicologoController {

    private final SecreService secreService;

    public PsicologoController(SecreService secreService) {
        this.secreService = secreService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<PsicologoResponse> registrarPsicologo(@RequestBody PsicologoRequest psicologoDto) {
        PsicologoResponse response = secreService.registrarPsicologo(psicologoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PsicologoResponse>> obtenerPsicologos() {
        return ResponseEntity.ok(secreService.obtenenerPsicologos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PsicologoResponse> actualizarPsicologo(
            @PathVariable Long id,
            @RequestBody PsicologoRequest dto) {
        return ResponseEntity.ok(secreService.actPsicologo(id, dto));
    }

}
