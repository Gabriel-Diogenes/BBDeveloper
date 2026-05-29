package br.com.intercomex.api_BBDeveloper.BBDeveloper.controller;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.auth.TokenResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.service.AuthService;
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
