package com.clinica.service;

import com.clinica.model.Secretaria;
import com.clinica.model.SecretariaPrincipal;
import com.clinica.repository.RepoSecretaria;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailService implements UserDetailsService {

    private final RepoSecretaria repoSecretaria;

    public MyUserDetailService(RepoSecretaria repoSecretaria) {
        this.repoSecretaria = repoSecretaria;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Secretaria secretaria = repoSecretaria.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        if(secretaria==null)throw new UsernameNotFoundException("User not found");

        return new SecretariaPrincipal(secretaria);
    }
}
