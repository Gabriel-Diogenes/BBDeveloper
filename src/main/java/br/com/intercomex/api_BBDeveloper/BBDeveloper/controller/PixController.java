package br.com.intercomex.api_BBDeveloper.BBDeveloper.controller;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixCobrancaRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.request.PixCobvRequestDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobListaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobrancaImediataDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobvListaResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.dto.pix.response.PixCobvResponseDTO;
import br.com.intercomex.api_BBDeveloper.BBDeveloper.service.PixService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pix")
@RequiredArgsConstructor
public class PixController {

    private final PixService pixService;

    @PostMapping("/cobrancas")
    public ResponseEntity<PixCobrancaImediataDTO> criarCobrancaTeste() {
        return ResponseEntity.ok(pixService.criarCobranca());
    }

    @PutMapping("/cob/{txid}")
    public ResponseEntity<PixCobrancaImediataDTO> criarCob(
            @PathVariable String txid,
            @RequestBody PixCobrancaRequestDTO request) {
        return ResponseEntity.ok(pixService.criarCob(txid, request));
    }

    @PostMapping("/cob")
    public ResponseEntity<PixCobrancaImediataDTO> criarCobSemTxid(
            @RequestBody PixCobrancaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pixService.criarCobSemTxid(request));
    }

    @GetMapping("/cob/{txid}")
    public ResponseEntity<PixCobrancaImediataDTO> consultarCob(@PathVariable String txid) {
        return ResponseEntity.ok(pixService.consultarCob(txid));
    }

    @PatchMapping("/cob/{txid}")
    public ResponseEntity<PixCobrancaImediataDTO> revisarCob(
            @PathVariable String txid,
            @RequestBody PixCobrancaRequestDTO request) {
        return ResponseEntity.ok(pixService.revisarCob(txid, request));
    }

    @DeleteMapping("/cob/{txid}")
    public ResponseEntity<PixCobrancaImediataDTO> cancelarCob(@PathVariable String txid) {
        return ResponseEntity.ok(pixService.cancelarCob(txid));
    }

    @GetMapping("/cob")
    public ResponseEntity<PixCobListaResponseDTO> listarCobs(
            @RequestParam(required = false) String inicio,
            @RequestParam(required = false) String fim,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String cnpj,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer paginaAtual,
            @RequestParam(required = false) Integer itensPorPagina) {
        return ResponseEntity.ok(pixService.listarCobs(
                inicio, fim, cpf, cnpj, status, paginaAtual, itensPorPagina));
    }

    @PutMapping("/cobv/{txid}")
    public ResponseEntity<PixCobvResponseDTO> criarCobv(
            @PathVariable String txid,
            @RequestBody PixCobvRequestDTO request) {
        return ResponseEntity.ok(pixService.criarCobv(txid, request));
    }

    @PostMapping("/cobv")
    public ResponseEntity<PixCobvResponseDTO> criarCobvSemTxid(
            @RequestBody PixCobvRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pixService.criarCobvSemTxid(request));
    }

    @GetMapping("/cobv/{txid}")
    public ResponseEntity<PixCobvResponseDTO> consultarCobv(@PathVariable String txid) {
        return ResponseEntity.ok(pixService.consultarCobv(txid));
    }

    @PatchMapping("/cobv/{txid}")
    public ResponseEntity<PixCobvResponseDTO> revisarCobv(
            @PathVariable String txid,
            @RequestBody PixCobvRequestDTO request) {
        return ResponseEntity.ok(pixService.revisarCobv(txid, request));
    }

    @DeleteMapping("/cobv/{txid}")
    public ResponseEntity<PixCobvResponseDTO> cancelarCobv(@PathVariable String txid) {
        return ResponseEntity.ok(pixService.cancelarCobv(txid));
    }

    @GetMapping("/cobv")
    public ResponseEntity<PixCobvListaResponseDTO> listarCobvs(
            @RequestParam(required = false) String inicio,
            @RequestParam(required = false) String fim,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String cnpj,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer paginaAtual,
            @RequestParam(required = false) Integer itensPorPagina) {
        return ResponseEntity.ok(pixService.listarCobvs(
                inicio, fim, cpf, cnpj, status, paginaAtual, itensPorPagina));
    }
}
