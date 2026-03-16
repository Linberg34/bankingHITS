package com.iisovaii.employee_bff.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "employee_settings")
@Getter
@Setter
@NoArgsConstructor
public class EmployeeSettings {

    @Id
    @Column(name = "employee_id")
    private UUID employeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "theme", nullable = false)
    private Theme theme = Theme.LIGHT;

    @ElementCollection
    @CollectionTable(
            name = "employee_hidden_accounts",
            joinColumns = @JoinColumn(name = "employee_id")
    )
    @Column(name = "account_id")
    private List<UUID> hiddenAccountIds = new ArrayList<>();

    public enum Theme {
        LIGHT, DARK
    }
}
