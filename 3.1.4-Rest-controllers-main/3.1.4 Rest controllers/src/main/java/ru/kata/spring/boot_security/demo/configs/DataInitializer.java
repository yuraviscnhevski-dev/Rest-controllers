package ru.kata.spring.boot_security.demo.configs;

import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer {

    private final UserService userService;
    private final RoleRepository roleRepository;

    @Autowired
    public DataInitializer(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void init() {
        System.out.println("===== ИНИЦИАЛИЗАЦИЯ ДАННЫХ =====");

        // Создаем роли, если их нет
        List<Role> adminRoles = roleRepository.findByName("ROLE_ADMIN");
        Role adminRole;
        if (adminRoles.isEmpty()) {
            adminRole = new Role("ROLE_ADMIN");
            adminRole = roleRepository.save(adminRole);
            System.out.println("Создана роль ADMIN");
        } else {
            adminRole = adminRoles.get(0);
            System.out.println("Роль ADMIN уже существует");
        }

        List<Role> userRoles = roleRepository.findByName("ROLE_USER");
        Role userRole;
        if (userRoles.isEmpty()) {
            userRole = new Role("ROLE_USER");
            userRole = roleRepository.save(userRole);
            System.out.println("Создана роль USER");
        } else {
            userRole = userRoles.get(0);
            System.out.println("Роль USER уже существует");
        }

        // Проверяем, есть ли уже админ
        if (userService.findByEmail("admin@mail.ru") == null) {
            User admin = new User("admin", "admin", 35, "admin@mail.ru", "admin");
            Set<Role> adminRolesSet = new HashSet<>();
            adminRolesSet.add(adminRole);
            adminRolesSet.add(userRole);
            admin.setRoles(adminRolesSet);
            userService.save(admin);
            System.out.println("Создан админ: admin@mail.ru");
        } else {
            System.out.println("Админ уже существует");
        }

        // Проверяем, есть ли уже пользователь
        if (userService.findByEmail("user@mail.ru") == null) {
            User user = new User("user", "user", 30, "user@mail.ru", "user");
            Set<Role> userRolesSet = new HashSet<>();
            userRolesSet.add(userRole);
            user.setRoles(userRolesSet);
            userService.save(user);
            System.out.println("Создан пользователь: user@mail.ru");
        } else {
            System.out.println("Пользователь уже существует");
        }

        System.out.println("===== ИНИЦИАЛИЗАЦИЯ ЗАВЕРШЕНА =====");
    }
}