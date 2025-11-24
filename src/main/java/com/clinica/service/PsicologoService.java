package com.clinica.service;

import com.clinica.dto.psicologo.PsicologoRequest;
import com.clinica.dto.psicologo.PsicologoResponse;
import com.clinica.exceptions.PsicologoNotFoundException;
import com.clinica.mapper.PsicologoMapper;
import com.clinica.model.Estado;
import com.clinica.model.Psicologo;
import com.clinica.model.Rol;
import com.clinica.model.User;
import com.clinica.repository.PsicologoRepository;
import com.clinica.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.clinica.mapper.PsicologoMapper.toEntity;
import static com.clinica.mapper.PsicologoMapper.toResponse;

@Service
public class PsicologoService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder;
    private final PsicologoRepository psicologoRepo;

    public PsicologoService(UserRepository userRepo, PsicologoRepository psicologoRepo) {
        this.userRepo = userRepo;
        this.psicologoRepo = psicologoRepo;
        this.encoder = new BCryptPasswordEncoder(12);
    }

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

    public List<PsicologoResponse> obtenenerPsicologos() {
        List<Psicologo> psicologos =    psicologoRepo.findByEstado(Estado.ACTIVO);

        return psicologos.stream()
                .map(PsicologoMapper::toResponse)
                .toList();
    }

    @Transactional
    public PsicologoResponse actPsicologo(Long id, PsicologoRequest dto) {
        Psicologo psicologo = psicologoRepo.findById(id)
                .orElseThrow(() -> new PsicologoNotFoundException("Psicólogo no encontrado con id " + id));

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
    public void desactivarPsicologo(Long id) {
        Psicologo psicologo = psicologoRepo.findById(id)
                .orElseThrow(() -> new PsicologoNotFoundException(
                        "Psicólogo no encontrado con id " + id));

        psicologo.setEstado(Estado.INACTIVO);
        psicologoRepo.save(psicologo);
    }


}
