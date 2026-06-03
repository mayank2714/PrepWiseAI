package com.backend.prepjob.repo;

import com.backend.prepjob.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, String> {

    Optional<User> findByUsernameOrEmail(String userName, String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

}
