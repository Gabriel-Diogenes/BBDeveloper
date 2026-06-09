package br.com.intercomex.api_BBDeveloper.BBDeveloper.controller;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoListaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoPixResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.cobranca.response.BoletoResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.service.CobrancaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cobranca")
@RequiredArgsConstructor
public class CobrancaController {

    private final CobrancaService cobrancaService;

    @GetMapping("/boletos")
    public ResponseEntity<BoletoListaResponseDTO> listarBoletos(
            @RequestParam Integer numeroConvenio) {
        return ResponseEntity.ok(cobrancaService.listarBoletos(numeroConvenio));
    }

    @PostMapping("/boletos")
    public ResponseEntity<BoletoResponseDTO> registrarBoleto(
            @RequestParam Integer numeroConvenio,
            @RequestParam String nomePagador,
            @RequestParam String cpfCnpj,
            @RequestParam Double valor,
            @RequestParam(defaultValue = "30") Integer diasVencimento,
            @RequestParam(defaultValue = "true") Boolean comPix) {
        return ResponseEntity.ok(
                cobrancaService.registrarBoleto(numeroConvenio, nomePagador, cpfCnpj, valor, diasVencimento, comPix));
    }

    @GetMapping("/boletos/{numeroBoleto}/pix")
    public ResponseEntity<BoletoPixResponseDTO> consultarPixBoleto(
            @PathVariable String numeroBoleto) {
        return ResponseEntity.ok(cobrancaService.consultarPixBoleto(numeroBoleto));
    }

    @PostMapping("/boletos/{numeroBoleto}/pix")
    public ResponseEntity<BoletoPixResponseDTO> gerarPixBoleto(
            @PathVariable String numeroBoleto) {
        return ResponseEntity.ok(cobrancaService.gerarPixBoleto(numeroBoleto));
    }

    @DeleteMapping("/boletos/{numeroBoleto}/pix")
    public ResponseEntity<Void> cancelarPixBoleto(
            @PathVariable String numeroBoleto) {
        cobrancaService.cancelarPixBoleto(numeroBoleto);
        return ResponseEntity.noContent().build();
    }
}
