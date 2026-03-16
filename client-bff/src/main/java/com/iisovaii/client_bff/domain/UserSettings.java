package com.iisovaii.client_bff.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_settings")
@Getter
@Setter
@NoArgsConstructor
@Builder
public class UserSettings {
    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "theme", nullable = false, length = 16)
    private Theme theme;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_hidden_accounts",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "account_id", nullable = false)
    private List<UUID> hiddenAccountIds = new ArrayList<>();

    public UserSettings(UUID userId, Theme theme, List<UUID> hiddenAccountIds) {
        this.userId = userId;
        this.theme = theme;
        this.hiddenAccountIds = hiddenAccountIds != null ? new ArrayList<>(hiddenAccountIds) : new ArrayList<>();
    }
}

