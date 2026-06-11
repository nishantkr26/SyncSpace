package com.nishant.auth_service.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.nishant.auth_service.entity.User;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    boolean existsByEmail(String email);

    Optional<User>findByEmail(String email);
}