package ru.mail.evmenova.springbootapp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.mail.evmenova.springbootapp.model.User;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {
    List<User> findByFirstName(String firstName);
    User findByEmail(String email);
}
