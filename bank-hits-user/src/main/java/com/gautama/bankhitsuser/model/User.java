package com.gautama.bankhitsuser.model;

import com.gautama.bankhitsuser.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String email;
    private Status status = Status.ACTIVE;
    private LocalDateTime registeredAt = LocalDate.now().atStartOfDay();

//    public boolean isAccountNonLocked() {
//        return status != Status.BANNED;
//    }
//
//    public String getUsername() {
//        return email;
//    }
}
