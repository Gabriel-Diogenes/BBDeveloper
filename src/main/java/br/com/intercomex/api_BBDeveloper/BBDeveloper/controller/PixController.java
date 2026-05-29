package br.com.intercomex.api_BBDeveloper.BBDeveloper.controller;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobrancaImediataDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.service.PixService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de Pix.
 * 
 * Responsável por:
 * - Receber requisições HTTP
 * - Validar entrada
 * - Retornar responses
 * 
 * Sem lógica de negócio (delegada ao Service).
 */
@RestController
@RequestMapping("/pix")
@RequiredArgsConstructor
public class PixController {

    private final PixService pixService;

    /**
     * Cria uma nova cobrança Pix imediata.
     * 
     * @return Dados da cobrança criada
     */
    @PostMapping("/cobrancas")
    public ResponseEntity<PixCobrancaImediataDTO> criarCobranca() {
        return ResponseEntity.ok(pixService.criarCobranca());
    }
}
