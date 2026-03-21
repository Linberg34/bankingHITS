//package com.iisovaii.employee_bff.controller;
//
//import com.iisovaii.employee_bff.dto.auth.LoginUrlResponse;
//import com.iisovaii.employee_bff.service.AuthService;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/bff/employee/auth")
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final AuthService authService;
//
//    @GetMapping("/login-url")
//    public ResponseEntity<LoginUrlResponse> getLoginUrl() {
//        return ResponseEntity.ok(authService.buildLoginUrl());
//    }
//
//    @GetMapping("/callback")
//    public ResponseEntity<Void> callback(
//            @RequestParam String code,
//            HttpServletResponse response) {
//
//        authService.exchangeCodeAndSetCookie(code, response);
//
//        return ResponseEntity
//                .status(HttpStatus.FOUND)
//                .header(HttpHeaders.LOCATION, "http://localhost:4201/")
//                .build();
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<Void> logout(
//            HttpServletRequest request,
//            HttpServletResponse response) {
//
//        authService.logout(request, response);
//        return ResponseEntity.ok().build();
//    }
//}