package com.gautama.bankhitsuser.client;

import com.gautama.bankhitsuser.dto.AccountDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "core", url = "${clients.core.url}")
public interface CoreClient {
    @GetMapping("/internal/accounts/my")
    List<AccountDTO> myAccounts();

    @GetMapping("/internal/accounts/by-user/{userId}")
    List<AccountDTO> accountsByUser(@PathVariable Long userId);
}