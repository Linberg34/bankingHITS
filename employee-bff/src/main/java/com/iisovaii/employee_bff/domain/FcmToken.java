package com.iisovaii.employee_bff.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "employee_fcm_tokens")
@Getter
@Setter
@NoArgsConstructor
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public FcmToken(UUID employeeId, String token) {
        this.employeeId = employeeId;
        this.token = token;
    }
}
