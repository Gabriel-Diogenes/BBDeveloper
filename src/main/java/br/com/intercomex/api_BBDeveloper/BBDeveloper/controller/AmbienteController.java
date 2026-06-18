package br.com.intercomex.api_BBDeveloper.BBDeveloper.controller;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.config.AmbienteResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
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
