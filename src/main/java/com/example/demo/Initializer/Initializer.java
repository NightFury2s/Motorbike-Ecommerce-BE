package com.example.demo.Initializer;


import com.example.demo.model.entity.DAOUser;
import com.example.demo.model.entity.Role;
import com.example.demo.model.entity.TypeProduct;
import com.example.demo.repositories.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Initializer implements ApplicationRunner {
    private final RoleRepository roleRepository;
    private final PasswordEncoder bcryptEncoder;
    private final TypeProductRepository typeProductRepository;
    private final UserRepository userRepository;

    public Initializer(RoleRepository roleRepository, PasswordEncoder bcryptEncoder, TypeProductRepository typeProductRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.bcryptEncoder = bcryptEncoder;
        this.typeProductRepository = typeProductRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!roleRepository.existsByRole("ADMIN") && !roleRepository.existsByRole("USER")) {
            AddRole("ADMIN");
            AddRole("USER");
            System.out.println("Đã thêm role ");
        }
        if (!userRepository.existsByUsername("admin1")) {
            addUserAdmin("admin1", "admin1", "kcosten1011231232131@gmail.com");
            System.out.println("đã thêm tk admin");
        }
        if (typeProductRepository.existsByNameType("Xe máy") && typeProductRepository.existsByNameType("Phụ tùng")) {
            addType("Xe máy");
            addType("Phụ tùng");
            System.out.println("Đã thêm type product ");
        }
    }

    private void AddRole(String role) {
        Role addRole = new Role();
        addRole.setRole(role);
        roleRepository.save(addRole);
    }

    private void addType(String type) {
        TypeProduct typeProduct = new TypeProduct();
        typeProduct.setNameType(type);
        typeProductRepository.save(typeProduct);
    }

    void addUserAdmin(String username, String password, String email) {
        DAOUser user = new DAOUser();
        user.setUsername(username);
        user.setPassword(bcryptEncoder.encode(password));
        user.setEmail(email);
        Role role = new Role();
        role.setId(1L);
        user.setRole(role);
        userRepository.save(user);
    }
}
