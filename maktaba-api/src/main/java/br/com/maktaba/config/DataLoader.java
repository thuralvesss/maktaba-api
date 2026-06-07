package br.com.maktaba.config;

import br.com.maktaba.model.Role;
import br.com.maktaba.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            Role admin = new Role();
            admin.setNome("ROLE_ADMIN");

            Role user = new Role();
            user.setNome("ROLE_USER");

            roleRepository.save(admin);
            roleRepository.save(user);

            System.out.println(" Roles inseridas com sucesso!");
        }
    }
}