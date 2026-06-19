package com.intercomex.api_bbdeveloper.controller;

import com.intercomex.api_bbdeveloper.dto.auth.TokenResponseDTO;
import com.intercomex.api_bbdeveloper.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthService authService;
    @PostMapping("/token")
    public ResponseEntity<TokenResponseDTO> gerarToken() {
        return ResponseEntity.ok(authService.gerarToken());
    }
}
