package com.iisovaii.employee_bff.repository;

import com.iisovaii.employee_bff.domain.EmployeeSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeSettingsRepository
        extends JpaRepository<EmployeeSettings, UUID> {

    Optional<EmployeeSettings> findByEmployeeId(UUID employeeId);
}
