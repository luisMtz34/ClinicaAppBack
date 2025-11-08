package com.clinica.service;

import com.clinica.dto.cita.CitaRequestDTO;
import com.clinica.dto.cita.CitaResponseDTO;
import com.clinica.exceptions.*;
import com.clinica.mapper.CitaMapper;
import com.clinica.model.*;
import com.clinica.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.clinica.mapper.CitaMapper.toResponse;

@Service
public class CitaService {

    private final PsicologoRepository psicologoRepo;
    private final PacienteRepository pacienteRepo;
    private final UserRepository userRepo;
    private final SecreRepository secreRepository;
    private final CitaRepository citaRepository;
    private final PagoRepository pagoRepository;

    public CitaService(PsicologoRepository psicologoRepo, PacienteRepository pacienteRepo, UserRepository userRepo,
                       SecreRepository secreRepository, CitaRepository citaRepository, PagoRepository pagoRepository) {
        this.psicologoRepo = psicologoRepo;
        this.pacienteRepo = pacienteRepo;
        this.userRepo = userRepo;
        this.secreRepository = secreRepository;
        this.citaRepository = citaRepository;
        this.pagoRepository = pagoRepository;
    }

    public CitaResponseDTO registrarCita(CitaRequestDTO dto) {
        Psicologo psicologo = psicologoRepo.findById(dto.getPsicologoId())
                .orElseThrow(() -> new PsicologoNotFoundException("Psicólogo no encontrado"));

        Paciente paciente = pacienteRepo.findById(dto.getPacienteId())
                .orElseThrow(() -> new PacienteNotFoundException("Paciente no encontrado"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        Secretaria secretaria = secreRepository.findByUser(user)
                .orElseThrow(() -> new SecretariaNotFoundException("Secretaria no encontrada"));

        Cita cita = CitaMapper.toEntity(dto, psicologo, paciente, secretaria);

        boolean citaExiste = citaRepository.existsByPacienteAndFecha(cita.getPaciente(), cita.getFecha());
        if (citaExiste) {
            throw new CitaDuplicadaException("El paciente ya tiene una cita registrada en esta fecha");
        }

        Cita citaGuardada = citaRepository.save(cita);
// Obtener pagos existentes, si los hubiera
        citaGuardada.setPagos(pagoRepository.findByCita(citaGuardada));
        return CitaMapper.toResponse(citaGuardada);

    }

    public List<CitaResponseDTO> obtenerCitas() {
        List<Cita> citas = citaRepository.findAll();
        return citas.stream().map(CitaMapper::toResponse).toList();
    }

    public CitaResponseDTO actCita(int id, CitaRequestDTO dto) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new CitaNotFoundException("Cita no encontrada"));

        // 🚫 No se puede modificar una cita atendida o cancelada
        if (cita.getEstado() == Estado.ATENDIDA || cita.getEstado() == Estado.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede modificar una cita que ya fue atendida o cancelada");
        }

        Psicologo psicologo = psicologoRepo.findById(dto.getPsicologoId())
                .orElseThrow(() -> new PsicologoNotFoundException("Psicólogo no encontrado"));

        Paciente paciente = pacienteRepo.findById(dto.getPacienteId())
                .orElseThrow(() -> new PacienteNotFoundException("Paciente no encontrado"));

        cita.setFecha(dto.getFecha());
        cita.setHora(dto.getHora());
        cita.setConsultorio(dto.getConsultorio());
        cita.setTipo(dto.getTipo());
        cita.setObservaciones(dto.getObservaciones());
        cita.setPsicologo(psicologo);
        cita.setPaciente(paciente);

        citaRepository.save(cita);
        citaRepository.save(cita);
        cita.setPagos(pagoRepository.findByCita(cita));
        return toResponse(cita);

    }


    public CitaResponseDTO cambiarEstadoCita(int citaId, Estado nuevoEstado) {
        Cita cita = citaRepository.findByIdWithPagos(citaId)
                .orElseThrow(() -> new CitaNotFoundException("Cita no encontrada"));

        if (cita.getEstado() == Estado.ATENDIDA || cita.getEstado() == Estado.CANCELADA || cita.getEstado() == Estado.NO_ASISTIO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede cambiar el estado de una cita ya atendida o cancelada");
        }

        cita.setEstado(nuevoEstado);
        citaRepository.save(cita);

        Pago pagoGenerado = null;

        if (nuevoEstado == Estado.ATENDIDA) {
            double montoBase = calcularMonto(cita);

            // Obtener todas las penalizaciones no aplicadas del paciente
            List<Pago> penalizacionesPendientes = pagoRepository
                    .findPenalizacionesNoAplicadasPorPaciente(cita.getPaciente().getClave(), TipoPago.PENALIZACION);

            int totalPenalizaciones = 0;
            for (Pago penal : penalizacionesPendientes) {
                totalPenalizaciones += penal.getPenalizacion();
                penal.setAplicado(true);
                pagoRepository.save(penal);
            }

            // Crear pago normal sumando todas las penalizaciones
            Pago pagoAtendida = new Pago();
            pagoAtendida.setCita(cita);
            pagoAtendida.setFecha(LocalDateTime.now());
            pagoAtendida.setAplicado(false);
            pagoAtendida.setMontoTotal(calcularMonto(cita) + totalPenalizaciones);
            pagoAtendida.setComisionClinica(cita.getPsicologo().getComision() * calcularMonto(cita) / 100);
            pagoAtendida.setTipoPago(TipoPago.PAGO_NORMAL);
            pagoAtendida.setMotivo("Cita atendida");
            pagoAtendida.setObservaciones("Pago normal generado para cita atendida");
            pagoRepository.save(pagoAtendida);
        } else if (nuevoEstado == Estado.NO_ASISTIO) {
            boolean yaExistePenal = cita.getPagos().stream()
                    .anyMatch(p -> p.getTipoPago() == TipoPago.PENALIZACION);

            if (!yaExistePenal) {
                Pago pagoNoAsistio = new Pago();
                pagoNoAsistio.setCita(cita);
                pagoNoAsistio.setFecha(LocalDateTime.now());
                pagoNoAsistio.setAplicado(false);
                pagoNoAsistio.setPenalizacion(200);
                pagoNoAsistio.setMontoTotal(200);
                pagoNoAsistio.setComisionClinica(0);
                pagoNoAsistio.setTipoPago(TipoPago.PENALIZACION);
                pagoNoAsistio.setMotivo("No asistencia");
                pagoNoAsistio.setObservaciones("Penalización por no asistencia");
                pagoRepository.save(pagoNoAsistio);

                pagoGenerado = pagoNoAsistio;
            }
        }

        cita.setPagos(pagoRepository.findByCita(cita));

        Pago pagoNormal = cita.getPagos().stream()
                .filter(p -> p.getTipoPago() == TipoPago.PAGO_NORMAL)
                .max((p1, p2) -> p1.getFecha().compareTo(p2.getFecha()))
                .orElse(null);

// Asignar al DTO
        CitaResponseDTO dto = CitaMapper.toResponse(cita);
        if (pagoNormal != null) {
            dto.setPagoInicialId(pagoNormal.getIdPagos());
            dto.setPagoInicialMonto(pagoNormal.getMontoTotal());
        } else {
            dto.setPagoInicialId(null);
            dto.setPagoInicialMonto(0.0);
        }
        if (pagoNormal != null) {
            dto.setPagoInicialId(pagoNormal.getIdPagos());
            dto.setPagoInicialMonto(pagoNormal.getMontoTotal());
        } else {
            dto.setPagoInicialId(null);
            dto.setPagoInicialMonto(0.0);
        }

// Calcular penalizaciones pendientes
        double penalizacionPendiente = cita.getPagos().stream()
                .filter(p -> p.getTipoPago() == TipoPago.PENALIZACION && !p.isAplicado())
                .mapToDouble(Pago::getPenalizacion)
                .sum();
        dto.setPenalizacionPendiente(penalizacionPendiente);

// Total de pagos
        double total = cita.getPagos().stream()
                .mapToDouble(Pago::getMontoTotal)
                .sum();
        dto.setTotal(total);

        return dto;

    }


    private double calcularMonto(Cita cita) {
        switch (cita.getTipo()) {
            case TipoCita.PRIMERA_VEZ:
                return 500;
            case TipoCita.SEGUIMIENTO:
                return 300;
            default:
                return 0;
        }
    }


    public CitaResponseDTO findByIdWithPagos(int id) {
        Cita cita = citaRepository.findByIdWithPagos(id)
                .orElseThrow(() -> new CitaNotFoundException("Cita no encontrada"));

        // 🔸 Obtener penalizaciones aplicadas al paciente en citas previas
        List<Pago> penalizacionesAplicadas = pagoRepository
                .findPenalizacionesAplicadasPorPaciente(cita.getPaciente().getClave())
                .stream()
                .filter(p -> p.isAplicado()) // solo las ya aplicadas
                .toList();

        // 🔸 Combinar los pagos de la cita actual con penalizaciones previas aplicadas
        List<Pago> todosPagos = new ArrayList<>();

        // penalizaciones aplicadas antes de esta cita
        for (Pago p : penalizacionesAplicadas) {
            if (p.getCita().getIdCitas() != cita.getIdCitas()) {
                todosPagos.add(p);
            }
        }

        // pagos propios de esta cita
        todosPagos.addAll(cita.getPagos());

        // 🔸 Ordenar: primero penalizaciones, luego pagos normales
        todosPagos.sort((p1, p2) -> {
            if (p1.getTipoPago() == TipoPago.PENALIZACION && p2.getTipoPago() != TipoPago.PENALIZACION) return -1;
            if (p1.getTipoPago() != TipoPago.PENALIZACION && p2.getTipoPago() == TipoPago.PENALIZACION) return 1;
            return p1.getFecha().compareTo(p2.getFecha());
        });

        // 🔸 Calcular total
        double total = todosPagos.stream()
                .mapToDouble(Pago::getMontoTotal)
                .sum();

        cita.setPagos(todosPagos);

        CitaResponseDTO dto = CitaMapper.toResponse(cita);
        dto.setTotal(total);
        return dto;
    }


}
