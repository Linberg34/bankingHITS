//package com.iisovaii.client_bff.controller;
//
//import com.iisovaii.client_bff.dto.auth.LoginUrlResponse;
//import com.iisovaii.client_bff.service.AuthService;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
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
//@RequestMapping("/bff/client/auth")
//@RequiredArgsConstructor
//@Tag(name = "Auth", description = "OAuth2 / SSO аутентификация клиента")
//public class AuthController {
//
//    private final AuthService authService;
//
//    @GetMapping("/login-url")
//    @Operation(
//            summary = "Получить URL для логина через SSO",
//            description = "Возвращает ссылку для редиректа на страницу аутентификации SSO (OAuth2 Authorization Code Flow)."
//    )
//    public ResponseEntity<LoginUrlResponse> getLoginUrl() {
//        return ResponseEntity.ok(authService.buildLoginUrl());
//    }
//
//    @GetMapping("/callback")
//    @Operation(
//            summary = "Callback от SSO с authorization code",
//            description = "Принимает authorization code от SSO, обменивает его на access_token, устанавливает httpOnly cookie и делает редирект на клиентское приложение."
//    )
//    public ResponseEntity<Void> callback(
//            @RequestParam String code,
//            HttpServletResponse response) {
//        authService.exchangeCodeAndSetCookie(code, response);
//        // редиректим обратно в Angular
//        return ResponseEntity.status(HttpStatus.FOUND)
//                .header(HttpHeaders.LOCATION, "/")
//                .build();
//    }
//
//    @PostMapping("/logout")
//    @Operation(
//            summary = "Logout клиента",
//            description = "Очищает httpOnly cookie с access_token и завершает сессию на стороне BFF."
//    )
//    public ResponseEntity<Void> logout(
//            HttpServletRequest request,
//            HttpServletResponse response) {
//        authService.logout(request, response);
//        return ResponseEntity.ok().build();
//    }
//}
