package com.intercomex.api_bbdeveloper.controller;

import com.intercomex.api_bbdeveloper.dto.auth.TokenResponseDTO;
import com.intercomex.api_bbdeveloper.service.AuthService;
import com.intercomex.api_bbdeveloper.support.TestFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthenticationController controller;

    @Test
    void gerarToken_retornaTokenDoServico() {
        TokenResponseDTO token = TestFixtures.token();
        when(authService.gerarToken()).thenReturn(token);

        ResponseEntity<TokenResponseDTO> response = controller.gerarToken();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(token, response.getBody());
        verify(authService).gerarToken();
    }
}
