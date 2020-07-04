package ru.mail.evmenova.springbootapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.mail.evmenova.springbootapp.model.Role;

public interface RoleRepository extends CrudRepository<Role, Integer> {
    Role findByRoleName(String roleName);
}
