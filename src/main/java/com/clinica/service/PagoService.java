package com.clinica.service;

import com.clinica.dto.pago.PagoRequestDTO;
import com.clinica.dto.pago.PagoResponseDTO;
import com.clinica.exceptions.CitaNotFoundException;
import com.clinica.mapper.PagoMapper;
import com.clinica.model.*;
import com.clinica.repository.CitaRepository;
import com.clinica.repository.PagoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final CitaRepository citaRepository;

    public PagoResponseDTO registrarPago(PagoRequestDTO dto) {
        Cita cita = citaRepository.findById(dto.getCitaId())
                .orElseThrow(() -> new CitaNotFoundException("Cita no encontrada"));

        Pago pago = new Pago();
        pago.setFecha(LocalDateTime.now());
        pago.setMotivo(dto.getMotivo());
        pago.setObservaciones(dto.getObservaciones());
        pago.setTipoPago(dto.getTipoPago());
        pago.setCita(cita);

        // 🔹 Lógica diferenciada según el tipo de cita / pago
        if (dto.getTipoPago() == TipoPago.PENALIZACION) {
            // Cita con estado NO_ASISTIO
            pago.setMontoTotal(dto.getPenalizacion());
            pago.setPenalizacion(dto.getPenalizacion());
            pago.setComisionClinica(0);
            pago.setAplicado(false); // aún no se aplica
        } else {
            // Cita con estado ATENDIDA (pago normal)
            double penalizacionesPendientes = obtenerPenalizacionesPendientes(cita.getPaciente());
            pago.setPenalizacion(penalizacionesPendientes);
            pago.setMontoTotal(dto.getMontoTotal() + penalizacionesPendientes);
            pago.setComisionClinica(dto.getComisionClinica());
            pago.setAplicado(true);

            // Si había penalizaciones pendientes, marcarlas como aplicadas
            if (penalizacionesPendientes > 0) {
                aplicarPenalizacionesPendientes(cita.getPaciente());
            }
        }

        pagoRepository.saveAndFlush(pago);
        return PagoMapper.toResponse(pago);
    }

    private double obtenerPenalizacionesPendientes(Paciente paciente) {
        List<Pago> pendientes = pagoRepository.findPenalizacionesPendientesPorPaciente(paciente.getClave());
        return pendientes.stream()
                .mapToDouble(Pago::getPenalizacion)
                .sum();
    }

    private void aplicarPenalizacionesPendientes(Paciente paciente) {
        List<Pago> pendientes = pagoRepository.findPenalizacionesPendientesPorPaciente(paciente.getClave());
        pendientes.forEach(p -> p.setAplicado(true));
        pagoRepository.saveAll(pendientes);
    }

    public PagoResponseDTO obtenerPagoPorId(int id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        return PagoMapper.toResponse(pago);
    }

    public List<PagoResponseDTO> listarPagos() {
        return pagoRepository.findAll()
                .stream()
                .map(PagoMapper::toResponse)
                .toList();
    }

    public List<PagoResponseDTO> listarPagosPorCita(int idCita) {
        return pagoRepository.findByCita_IdCitas(idCita)
                .stream()
                .map(PagoMapper::toResponse)
                .collect(Collectors.toList());
    }

}
