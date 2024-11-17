package de.dkeiss.taxishare.config;

import de.dkeiss.taxishare.persistence.RoleRepository;
import de.dkeiss.taxishare.persistence.model.Role;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitRoles {

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void init() {
        if (!roleRepository.existsByName("user")) {
            roleRepository.save(new Role(1L, "user"));
        }
        if (!roleRepository.existsByName("admin")) {
            roleRepository.save(new Role(2L, "admin"));
        }
    }

}
