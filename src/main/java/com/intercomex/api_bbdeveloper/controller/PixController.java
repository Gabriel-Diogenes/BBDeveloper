package com.intercomex.api_bbdeveloper.controller;

import com.intercomex.api_bbdeveloper.dto.pix.request.PixCobrancaRequestDTO;
import com.intercomex.api_bbdeveloper.dto.pix.request.PixCobvRequestDTO;
import com.intercomex.api_bbdeveloper.dto.pix.request.PixDevolucaoRequestDTO;
import com.intercomex.api_bbdeveloper.dto.pix.response.PixCobListaResponseDTO;
import com.intercomex.api_bbdeveloper.dto.pix.response.PixCobrancaImediataDTO;
import com.intercomex.api_bbdeveloper.dto.pix.response.PixCobvListaResponseDTO;
import com.intercomex.api_bbdeveloper.dto.pix.response.PixCobvResponseDTO;
import com.intercomex.api_bbdeveloper.dto.pix.response.PixDevolucaoDTO;
import com.intercomex.api_bbdeveloper.dto.pix.response.PixRecebidoDTO;
import com.intercomex.api_bbdeveloper.dto.pix.response.PixRecebidoListaResponseDTO;
import com.intercomex.api_bbdeveloper.service.PixService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Webhook Pix: não implementado nesta API — será tratado separadamente no Oracle APEX.
 */
@RestController
@RequestMapping("/pix")
@RequiredArgsConstructor
public class PixController {

    private final PixService pixService;

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

    @GetMapping
    public ResponseEntity<PixRecebidoListaResponseDTO> listarPixRecebidos(
            @RequestParam(required = false) String inicio,
            @RequestParam(required = false) String fim,
            @RequestParam(required = false) String txid,
            @RequestParam(required = false) Boolean txIdPresente,
            @RequestParam(required = false) Boolean devolucaoPresente,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String cnpj,
            @RequestParam(required = false) Integer paginaAtual,
            @RequestParam(required = false) Integer itensPorPagina) {
        return ResponseEntity.ok(pixService.listarPixRecebidos(
                inicio, fim, txid, txIdPresente, devolucaoPresente, cpf, cnpj, paginaAtual, itensPorPagina));
    }

    @GetMapping("/{e2eid:E[a-zA-Z0-9]{31}}")
    public ResponseEntity<PixRecebidoDTO> consultarPixRecebido(@PathVariable String e2eid) {
        return ResponseEntity.ok(pixService.consultarPixRecebido(e2eid));
    }

    @PutMapping("/{e2eid:E[a-zA-Z0-9]{31}}/devolucao/{id}")
    public ResponseEntity<PixDevolucaoDTO> solicitarDevolucao(
            @PathVariable String e2eid,
            @PathVariable String id,
            @RequestBody PixDevolucaoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pixService.solicitarDevolucao(e2eid, id, request));
    }

    @GetMapping("/{e2eid:E[a-zA-Z0-9]{31}}/devolucao/{id}")
    public ResponseEntity<PixDevolucaoDTO> consultarDevolucao(
            @PathVariable String e2eid,
            @PathVariable String id) {
        return ResponseEntity.ok(pixService.consultarDevolucao(e2eid, id));
    }
}
