package com.clinica.service;

import com.clinica.dto.pago.PagoRequestDTO;
import com.clinica.dto.pago.PagoResponseDTO;
import com.clinica.exceptions.CitaNotFoundException;
import com.clinica.mapper.PagoMapper;
import com.clinica.model.Cita;
import com.clinica.model.Pago;
import com.clinica.repository.CitaRepository;
import com.clinica.repository.PagoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        pago.setMontoTotal(dto.getMontoTotal());
        pago.setComisionClinica(dto.getComisionClinica());
        pago.setPenalizacion(dto.getPenalizacion());
        pago.setMotivo(dto.getMotivo());
        pago.setObservaciones(dto.getObservaciones());
        pago.setTipoPago(dto.getTipoPago());
        pago.setAplicado(false);
        pago.setCita(cita);

        pagoRepository.saveAndFlush(pago);
        return PagoMapper.toResponse(pago);
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

    public List<PagoResponseDTO> listarPorCita(Integer citaId) {
        return pagoRepository.findByCita_IdCitas(citaId)
                .stream()
                .map(PagoMapper::toResponse)
                .toList();
    }

    public List<PagoResponseDTO> listarPagosPorCita(int idCita) {
        List<Pago> pagos = pagoRepository.findByCita_IdCitas(idCita);
        return pagos.stream()
                .map(PagoMapper::toResponse)
                .collect(Collectors.toList());
    }


}
