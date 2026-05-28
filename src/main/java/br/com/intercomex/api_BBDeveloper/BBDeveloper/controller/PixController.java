package br.com.intercomex.api_BBDeveloper.BBDeveloper.controller;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.PixCobrancaImediata;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.service.PixService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pix")
@RequiredArgsConstructor
public class PixController {

    private final PixService pixService;

    /** POST /pix/cobrancas — cria uma nova cobrança Pix imediata */
    @PostMapping("/cobrancas")
    public ResponseEntity<PixCobrancaImediata> criarCobranca() {
        return ResponseEntity.ok(pixService.criarCobranca());
    }
}
