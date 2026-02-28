package com.gautama.bankhitsuser.controller;

import com.gautama.bankhitsuser.client.CoreClient;
import com.gautama.bankhitsuser.dto.AccountDTO;
import com.gautama.bankhitsuser.dto.UserDTO;
import com.gautama.bankhitsuser.dto.UserFullDTO;
import com.gautama.bankhitsuser.enums.UserQueryType;
import com.gautama.bankhitsuser.mapper.UserMapper;
import com.gautama.bankhitsuser.model.User;
import com.gautama.bankhitsuser.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final CoreClient coreClient;

    @GetMapping("/api/accounts")
    public List<AccountDTO> myAccounts() {
        return coreClient.myAccounts();
    }
}