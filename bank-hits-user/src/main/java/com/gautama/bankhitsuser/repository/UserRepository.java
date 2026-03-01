package com.gautama.bankhitsuser.repository;

import com.gautama.bankhitsuser.enums.Role;
import com.gautama.bankhitsuser.enums.Status;
import com.gautama.bankhitsuser.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findAllByRole(Role role);
    List<User> findAllByRoleIsNull();
    List<User> findAllByStatus(Status status);

}