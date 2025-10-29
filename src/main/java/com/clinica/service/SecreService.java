package com.clinica.service;

import com.clinica.dto.cita.CitaRequestDTO;
import com.clinica.dto.cita.CitaResponseDTO;
import com.clinica.dto.paciente.PacienteRequest;
import com.clinica.dto.paciente.PacienteResponse;
import com.clinica.dto.psicologo.PsicologoRequest;
import com.clinica.dto.psicologo.PsicologoResponse;
import com.clinica.exceptions.CitaDuplicadaException;
import com.clinica.mapper.CitaMapper;
import com.clinica.mapper.PacienteMapper;
import com.clinica.mapper.PsicologoMapper;
import com.clinica.model.*;
import com.clinica.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.clinica.mapper.PsicologoMapper.toEntity;
import static com.clinica.mapper.PsicologoMapper.toResponse;
import static com.clinica.mapper.PacienteMapper.toEntity;
import static com.clinica.mapper.PacienteMapper.toResponse;
import static com.clinica.mapper.CitaMapper.toResponse;


@Service
public class SecreService {

    private final UserRepository userRepo;
    private final PsicologoRepository psicologoRepo;
    private final PacienteRepository pacienteRepo;
    private final SecreRepository secreRepository;
    private final CitaRepository citaRepository;
    private final PagoRepository pagoRepository;

    private final BCryptPasswordEncoder encoder;

    public SecreService(UserRepository userRepo,
                        PsicologoRepository psicologoRepo,
                        PacienteRepository pacienteRepo, SecreRepository secreRepository, CitaRepository citaRepository, PagoRepository pagoRepository) {
        this.userRepo = userRepo;
        this.psicologoRepo = psicologoRepo;
        this.pacienteRepo = pacienteRepo;
        this.secreRepository = secreRepository;
        this.citaRepository = citaRepository;
        this.pagoRepository = pagoRepository;
        this.encoder = new BCryptPasswordEncoder(12);
    }

    //PSICOLOGO
    public PsicologoResponse registrarPsicologo(PsicologoRequest psicologoDto) {
        User user = new User();
        user.setEmail(psicologoDto.getEmail());
        user.setPassword(encoder.encode(psicologoDto.getPassword()));
        user.setFullName(psicologoDto.getNombre());
        user.setRol(Rol.PSICOLOGO);
        userRepo.save(user);

        Psicologo psicologo = toEntity(psicologoDto, user);
        psicologoRepo.save(psicologo);

        return toResponse(psicologo);
    }

    //PACIENTE
    public PacienteResponse registrarPaciente(PacienteRequest pacienteDto) {

        Paciente entity = toEntity(pacienteDto);
        entity.setEstado(Estado.ACTIVO);
        Paciente saved = pacienteRepo.save(entity);


        return toResponse(saved);
    }

    //CITA
    public CitaResponseDTO registrarCita(CitaRequestDTO dto) {
        Psicologo psicologo = psicologoRepo.findById(dto.getPsicologoId())
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado"));

        Paciente paciente = pacienteRepo.findById(dto.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Secretaria secretaria = secreRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Secretaria no encontrada"));

        Cita cita = CitaMapper.toEntity(dto, psicologo, paciente, secretaria);

        boolean citaExiste = citaRepository.existsByPacienteAndFecha(cita.getPaciente(), cita.getFecha());

        if(citaExiste){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El paciente ya tiene una cita registrada en esta fecha");
        }
        Cita citaGuardada = citaRepository.save(cita);
        return CitaMapper.toResponse(citaGuardada);

    }

    public CitaResponseDTO cambiarEstadoCita(int citaId, Estado nuevoEstado) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new CitaDuplicadaException("Cita no encontrada"));

        cita.setEstado(nuevoEstado);
        citaRepository.save(cita);

        // Si pasa a ATENDIDA, generar pago automáticamente
        if (nuevoEstado == Estado.ATENDIDA) {
            registrarPago(cita, 500);
        }


        return CitaMapper.toResponse(cita);
    }


    private Pago registrarPago(Cita cita, int montoBase) {
        if (pagoRepository.existsByCita(cita)) {
            throw new IllegalStateException("Ya existe un pago registrado para esta cita");
        }

        int penalizacionPendiente = calcularPenalizacionPendiente(cita.getPaciente());

        Psicologo psicologo = cita.getPsicologo();
        int comision = (int) ((montoBase + penalizacionPendiente) * psicologo.getComision() / 100);

        Pago pago = new Pago();
        pago.setFecha(LocalDateTime.now());
        pago.setCantidad(montoBase);
        pago.setPenalizacion(penalizacionPendiente);
        pago.setMotivo("Consulta psicológica");
        pago.setTipo("EFECTIVO");
        pago.setObservaciones(penalizacionPendiente > 0 ? "Incluye penalización" : "");
        pago.setComision(comision);
        pago.setCita(cita);

        Pago pagoGuardado = pagoRepository.save(pago);

        // Actualizar la lista de pagos de la cita
        if (cita.getPagos() == null) {
            cita.setPagos(new ArrayList<>());
        }
        cita.getPagos().add(pagoGuardado);

        return pagoGuardado;
    }

    public int calcularPenalizacionPendiente(Paciente paciente) {
        int penalizacionTotal = 0;

        List<Cita> citas = paciente.getCitas();

        for (Cita cita : citas) {
            if (cita.getEstado() == Estado.NO_ASISTIO) {
                boolean yaPagado = cita.getPagos() != null && cita.getPagos().stream()
                        .anyMatch(p -> p.getPenalizacion() > 0);

                if (!yaPagado) {
                    penalizacionTotal += 50;
                }
            }
        }
        return penalizacionTotal;
    }

    public List<CitaResponseDTO> obtenerCitas() {
        List<Cita> citas = citaRepository.findAll();

        return citas.stream()
                .map(CitaMapper::toResponse)
                .toList();
    }

    public List<PsicologoResponse> obtenenerPsicologos() {
        List<Psicologo> psicologos =    psicologoRepo.findAll();

        return psicologos.stream()
                .map(PsicologoMapper::toResponse)
                .toList();
    }

    public List<PacienteResponse> obtenerPacientes() {

        List<Paciente> pacientes = pacienteRepo.findAll();

        return pacientes.stream()
                .map(PacienteMapper::toResponse)
                .toList();
    }

    @Transactional
    public PsicologoResponse actPsicologo(Long id, PsicologoRequest dto) {
        Psicologo psicologo = psicologoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Psicólogo no encontrado con id " + id));

        psicologo.setTelefono(dto.getTelefono());
        psicologo.setComision(dto.getComision());


        User user = psicologo.getUser();
        if(user!=null){
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                user.setPassword(encoder.encode(dto.getPassword()));
            }
            user.setEmail(dto.getEmail());
            user.setFullName(dto.getNombre());
            userRepo.save(user);
        }

        psicologoRepo.save(psicologo);


        return toResponse(psicologo);
    }

    @Transactional
    public PacienteResponse actPaciente(String clave, PacienteRequest dto) {
        Paciente paciente = pacienteRepo.findById(clave)
                .orElseThrow(()-> new IllegalArgumentException("Paciente no encontrado"));

            paciente.setNombre(dto.getNombre());
            paciente.setFechaNac(dto.getFechaNac());
            paciente.setSexo(dto.getSexo());
            paciente.setTelefono(dto.getTelefono());
            paciente.setContacto(dto.getContacto());
            paciente.setParentesco(dto.getParentesco());
            paciente.setTelefonoCp(dto.getTelefonoCp());


        pacienteRepo.save(paciente);

        return toResponse(paciente);
    }

    public CitaResponseDTO actCita(int id, CitaRequestDTO dto) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Cita no encontrada"));


        Psicologo psicologo = psicologoRepo.findById(dto.getPsicologoId())
                .orElseThrow(()-> new RuntimeException("Psicologo no encontrado"));

        Paciente paciente = pacienteRepo.findById(dto.getPacienteId())
                        .orElseThrow(()->new RuntimeException("Paciente no encontrado"));

        cita.setFecha(dto.getFecha());
        cita.setHora(dto.getHora());
        cita.setConsultorio(dto.getConsultorio());
        cita.setTipo(dto.getTipo());
        cita.setObservaciones(dto.getObservaciones());
        cita.setPsicologo(psicologo);
        cita.setPaciente(paciente);


        citaRepository.save(cita);
        return toResponse(cita);
    }

    // 🔹 Citas de un día específico
    public List<CitaResponseDTO> obtenerCitasPorDia(String fechaStr) {
        LocalDate fecha = LocalDate.parse(fechaStr);
        List<Cita> citas = citaRepository.findByFecha(fecha);
        return citas.stream().map(CitaMapper::toResponse).toList();
    }

    // 🔹 Citas de una semana (inicio y fin)
    public List<CitaResponseDTO> obtenerCitasPorSemana(String inicioStr, String finStr) {
        LocalDate inicio = LocalDate.parse(inicioStr);
        LocalDate fin = LocalDate.parse(finStr);
        List<Cita> citas = citaRepository.findByFechaBetween(inicio, fin);
        return citas.stream().map(CitaMapper::toResponse).toList();
    }

    // 🔹 Citas de un mes
    public List<CitaResponseDTO> obtenerCitasPorMes(int anio, int mes) {
        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin = inicio.plusMonths(1).minusDays(1); // último día del mes
        List<Cita> citas = citaRepository.findByFechaBetween(inicio, fin);
        return citas.stream().map(CitaMapper::toResponse).toList();
    }

}
