package com.recruitment.engine.config;

import com.recruitment.engine.entity.Role;
import com.recruitment.engine.entity.enums.RoleType;
import com.recruitment.engine.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        for (RoleType type : RoleType.values()) {
            if (roleRepository.findByName(type).isEmpty()) {
                roleRepository.save(new Role(type));
            }
        }
    }
}