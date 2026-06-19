package com.intercomex.api_bbdeveloper.controller;

import com.intercomex.api_bbdeveloper.dto.config.AmbienteResponseDTO;
import com.intercomex.api_bbdeveloper.properties.BBApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AmbienteController {

    private final BBApiProperties properties;

    @GetMapping("/ambiente")
    public ResponseEntity<AmbienteResponseDTO> consultarAmbiente() {
        String mciHeader = properties.getExtratoHomologacaoHeader();
        return ResponseEntity.ok(new AmbienteResponseDTO(
                properties.getAmbiente(),
                properties.getOauthUrl(),
                properties.getPixBaseUrl(),
                properties.getCobrancaBaseUrl(),
                properties.getExtratoBaseUrl(),
                true,
                mciHeader != null && !mciHeader.isBlank(),
                properties.isPixRequerMtls()
        ));
    }
}
