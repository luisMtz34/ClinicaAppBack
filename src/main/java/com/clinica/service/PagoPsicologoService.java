package com.clinica.service;

import com.clinica.dto.pago.PagoResponseDTO;
import com.clinica.mapper.PagoMapper;
import com.clinica.model.Pago;
import com.clinica.model.Psicologo;
import com.clinica.repository.PagoRepository;
import com.clinica.repository.PsicologoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagoPsicologoService {

    private final PsicologoRepository psicologoRepository;
    private final PagoRepository pagoRepository;

    public PagoPsicologoService(
            PsicologoRepository psicologoRepository,
            PagoRepository pagoRepository
    ) {
        this.psicologoRepository = psicologoRepository;
        this.pagoRepository = pagoRepository;
    }

    public List<PagoResponseDTO> obtenerPagosPorPsicologo(String emailPsicologo) {

        Psicologo psicologo = psicologoRepository.findByUser_Email(emailPsicologo)
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado"));

        List<Pago> pagos = pagoRepository.findAll().stream()
                .filter(p -> p.getCita().getPsicologo().getIdPsicologo().equals(psicologo.getIdPsicologo()))
                .toList();

        return pagos.stream()
                .map(PagoMapper::toResponse)
                .toList();
    }

    public List<PagoResponseDTO> obtenerPagosPorCita(int idCita) {
        return pagoRepository.findByCita_IdCitasOrderByIdPagosDesc(idCita)
                .stream()
                .map(PagoMapper::toResponse)
                .toList();
    }
}
