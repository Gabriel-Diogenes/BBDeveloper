package br.com.intercomex.api_BBDeveloper.BBDeveloper.controller;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.extrato.response.ExtratoResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.service.ExtratoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/extrato")
@RequiredArgsConstructor
public class ExtratoController {

    private final ExtratoService extratoService;

    @GetMapping
    public ResponseEntity<ExtratoResponseDTO> consultarExtrato(
            @RequestParam String agencia,
            @RequestParam String conta,
            @RequestParam String dataInicio,
            @RequestParam String dataFim,
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer quantidadePorPagina) {
        return ResponseEntity.ok(extratoService.consultarExtrato(
                agencia, conta, dataInicio, dataFim, pagina, quantidadePorPagina));
    }
}
