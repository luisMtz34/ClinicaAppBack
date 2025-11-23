package com.clinica.controller;

import com.clinica.dto.pago.PagoRequestDTO;
import com.clinica.dto.pago.PagoResponseDTO;
import com.clinica.model.TipoPago;
import com.clinica.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('SECRETARIA')")
public class PagoController {
    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<PagoResponseDTO> registrarPago(@RequestBody PagoRequestDTO dto) {
        PagoResponseDTO response = pagoService.registrarPago(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Obtener pago por ID
    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> obtenerPagoPorId(@PathVariable int id) {
        PagoResponseDTO response = pagoService.obtenerPagoPorId(id);
        return ResponseEntity.ok(response);
    }

    // (opcional) listar todos los pagos registrados
    @GetMapping
    public ResponseEntity<List<PagoResponseDTO>> listarPagos() {
        return ResponseEntity.ok(pagoService.listarPagos());
    }


    @GetMapping("/cita/{idCita}")
    public ResponseEntity<List<PagoResponseDTO>> listarPagosPorCita(@PathVariable int idCita) {
        return ResponseEntity.ok(pagoService.listarPagosPorCita(idCita));
    }

    @GetMapping("/penalizaciones/{pacienteId}")
    public ResponseEntity<List<PagoResponseDTO>> obtenerPenalizacionesPendientes(
            @PathVariable String pacienteId
    ) {
        List<PagoResponseDTO> pendientes = pagoService.obtenerPenalizacionesPendientes(pacienteId);
        return ResponseEntity.ok(pendientes);
    }


}
