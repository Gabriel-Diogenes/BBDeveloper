package com.intercomex.api_bbdeveloper.controller;

import com.intercomex.api_bbdeveloper.dto.cobranca.request.BoletoAlterarRequestDTO;
import com.intercomex.api_bbdeveloper.dto.cobranca.request.BoletoRegistrarRequestDTO;
import com.intercomex.api_bbdeveloper.dto.cobranca.response.BoletoBaixaResponseDTO;
import com.intercomex.api_bbdeveloper.dto.cobranca.response.BoletoConsultaResponseDTO;
import com.intercomex.api_bbdeveloper.dto.cobranca.response.BoletoListaResponseDTO;
import com.intercomex.api_bbdeveloper.dto.cobranca.response.BoletoPixResponseDTO;
import com.intercomex.api_bbdeveloper.dto.cobranca.response.BoletoResponseDTO;
import com.intercomex.api_bbdeveloper.service.CobrancaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cobranca")
@RequiredArgsConstructor
public class CobrancaController {

    private final CobrancaService cobrancaService;

    @GetMapping("/boletos")
    public ResponseEntity<BoletoListaResponseDTO> listarBoletos(
            @RequestParam Integer numeroConvenio,
            @RequestParam String agenciaBeneficiario,
            @RequestParam String contaBeneficiario) {
        return ResponseEntity.ok(
                cobrancaService.listarBoletos(numeroConvenio, agenciaBeneficiario, contaBeneficiario));
    }

    @PostMapping("/boletos")
    public ResponseEntity<BoletoResponseDTO> registrarBoleto(
            @RequestBody BoletoRegistrarRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cobrancaService.registrarBoleto(request));
    }

    @PostMapping("/boletos/simplificado")
    public ResponseEntity<BoletoResponseDTO> registrarBoletoSimplificado(
            @RequestParam Integer numeroConvenio,
            @RequestParam String nomePagador,
            @RequestParam String cpfCnpj,
            @RequestParam Double valor,
            @RequestParam(defaultValue = "30") Integer diasVencimento,
            @RequestParam(defaultValue = "true") Boolean comPix) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                cobrancaService.registrarBoletoSimplificado(
                        numeroConvenio, nomePagador, cpfCnpj, valor, diasVencimento, comPix));
    }

    @GetMapping("/boletos/{numeroBoleto}")
    public ResponseEntity<BoletoConsultaResponseDTO> consultarBoleto(
            @PathVariable String numeroBoleto,
            @RequestParam Integer numeroConvenio) {
        return ResponseEntity.ok(cobrancaService.consultarBoleto(numeroBoleto, numeroConvenio));
    }

    @PatchMapping("/boletos/{numeroBoleto}")
    public ResponseEntity<BoletoResponseDTO> alterarBoleto(
            @PathVariable String numeroBoleto,
            @RequestBody BoletoAlterarRequestDTO request) {
        return ResponseEntity.ok(cobrancaService.alterarBoleto(numeroBoleto, request));
    }

    @PostMapping("/boletos/{numeroBoleto}/baixar")
    public ResponseEntity<BoletoBaixaResponseDTO> baixarBoleto(
            @PathVariable String numeroBoleto,
            @RequestParam Integer numeroConvenio) {
        return ResponseEntity.ok(cobrancaService.baixarBoleto(numeroBoleto, numeroConvenio));
    }

    @PostMapping("/boletos/{numeroBoleto}/cancelar")
    public ResponseEntity<BoletoBaixaResponseDTO> cancelarBoleto(
            @PathVariable String numeroBoleto,
            @RequestParam Integer numeroConvenio) {
        return ResponseEntity.ok(cobrancaService.cancelarBoleto(numeroBoleto, numeroConvenio));
    }

    @GetMapping("/boletos/{numeroBoleto}/pix")
    public ResponseEntity<BoletoPixResponseDTO> consultarPixBoleto(
            @PathVariable String numeroBoleto,
            @RequestParam Integer numeroConvenio) {
        return ResponseEntity.ok(cobrancaService.consultarPixBoleto(numeroBoleto, numeroConvenio));
    }

    @PostMapping("/boletos/{numeroBoleto}/pix")
    public ResponseEntity<BoletoPixResponseDTO> gerarPixBoleto(
            @PathVariable String numeroBoleto,
            @RequestParam Integer numeroConvenio) {
        return ResponseEntity.ok(cobrancaService.gerarPixBoleto(numeroBoleto, numeroConvenio));
    }

    @DeleteMapping("/boletos/{numeroBoleto}/pix")
    public ResponseEntity<Void> cancelarPixBoleto(
            @PathVariable String numeroBoleto,
            @RequestParam Integer numeroConvenio) {
        cobrancaService.cancelarPixBoleto(numeroBoleto, numeroConvenio);
        return ResponseEntity.noContent().build();
    }
}
