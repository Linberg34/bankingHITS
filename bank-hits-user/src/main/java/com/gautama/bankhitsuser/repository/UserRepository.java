package com.gautama.bankhitsuser.repository;

import com.gautama.bankhitsuser.enums.Status;
import com.gautama.bankhitsuser.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    List<User> findAllByStatus(Status status);
    boolean existsByEmail(String email);
}