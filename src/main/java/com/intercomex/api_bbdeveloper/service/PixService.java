package com.intercomex.api_bbdeveloper.service;

import com.intercomex.api_bbdeveloper.client.pix.PixApiClient;
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
import com.intercomex.api_bbdeveloper.util.PixUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PixService {

    private final PixApiClient pixApiClient;

    public PixCobrancaImediataDTO criarCob(String txid, PixCobrancaRequestDTO request) {
        String txidValido = resolverTxid(txid);
        log.debug("Criando Cob — txid: {}", txidValido);
        return pixApiClient.criarCob(txidValido, request);
    }

    public PixCobrancaImediataDTO criarCobSemTxid(PixCobrancaRequestDTO request) {
        log.debug("Criando Cob sem txid");
        return pixApiClient.criarCobSemTxid(request);
    }

    public PixCobrancaImediataDTO consultarCob(String txid) {
        PixUtil.validarTxid(txid);
        log.debug("Consultando Cob — txid: {}", txid);
        return pixApiClient.consultarCob(txid);
    }

    public PixCobrancaImediataDTO revisarCob(String txid, PixCobrancaRequestDTO request) {
        PixUtil.validarTxid(txid);
        log.debug("Revisando Cob — txid: {}", txid);
        return pixApiClient.revisarCob(txid, request);
    }

    public PixCobListaResponseDTO listarCobs(
            String inicio, String fim, String cpf, String cnpj, String status,
            Integer paginaAtual, Integer itensPorPagina) {
        String inicioResolvido = resolverPeriodoInicio(inicio);
        String fimResolvido = resolverPeriodoFim(fim);
        PixUtil.validarPeriodoListagem(inicioResolvido, fimResolvido);
        log.debug("Listando Cobs — período: {} a {}", inicioResolvido, fimResolvido);
        return pixApiClient.listarCobs(
                inicioResolvido, fimResolvido, cpf, cnpj, status, paginaAtual, itensPorPagina);
    }

    public PixCobvResponseDTO criarCobv(String txid, PixCobvRequestDTO request) {
        String txidValido = resolverTxid(txid);
        log.debug("Criando CobV — txid: {}", txidValido);
        return pixApiClient.criarCobv(txidValido, request);
    }

    public PixCobvResponseDTO consultarCobv(String txid) {
        PixUtil.validarTxid(txid);
        log.debug("Consultando CobV — txid: {}", txid);
        return pixApiClient.consultarCobv(txid);
    }

    public PixCobvResponseDTO revisarCobv(String txid, PixCobvRequestDTO request) {
        PixUtil.validarTxid(txid);
        log.debug("Revisando CobV — txid: {}", txid);
        return pixApiClient.revisarCobv(txid, request);
    }

    public PixCobvListaResponseDTO listarCobvs(
            String inicio, String fim, String cpf, String cnpj, String status,
            Integer paginaAtual, Integer itensPorPagina) {
        String inicioResolvido = resolverPeriodoInicio(inicio);
        String fimResolvido = resolverPeriodoFim(fim);
        PixUtil.validarPeriodoListagem(inicioResolvido, fimResolvido);
        log.debug("Listando CobVs — período: {} a {}", inicioResolvido, fimResolvido);
        return pixApiClient.listarCobvs(
                inicioResolvido, fimResolvido, cpf, cnpj, status, paginaAtual, itensPorPagina);
    }

    public PixRecebidoListaResponseDTO listarPixRecebidos(
            String inicio, String fim, String txid, Boolean txIdPresente, Boolean devolucaoPresente,
            String cpf, String cnpj, Integer paginaAtual, Integer itensPorPagina) {
        String inicioResolvido = resolverPeriodoInicio(inicio);
        String fimResolvido = resolverPeriodoFim(fim);
        PixUtil.validarPeriodoListagem(inicioResolvido, fimResolvido);
        log.debug("Listando Pix recebidos — período: {} a {}", inicioResolvido, fimResolvido);
        return pixApiClient.listarPixRecebidos(
                inicioResolvido, fimResolvido, txid, txIdPresente, devolucaoPresente,
                cpf, cnpj, paginaAtual, itensPorPagina);
    }

    public PixRecebidoDTO consultarPixRecebido(String e2eid) {
        PixUtil.validarE2eid(e2eid);
        log.debug("Consultando Pix recebido — e2eid: {}", e2eid);
        return pixApiClient.consultarPixRecebido(e2eid);
    }

    public PixDevolucaoDTO solicitarDevolucao(String e2eid, String id, PixDevolucaoRequestDTO request) {
        PixUtil.validarE2eid(e2eid);
        PixUtil.validarDevolucaoId(id);
        log.debug("Solicitando devolução Pix — e2eid: {}, id: {}", e2eid, id);
        return pixApiClient.solicitarDevolucao(e2eid, id, request);
    }

    public PixDevolucaoDTO consultarDevolucao(String e2eid, String id) {
        PixUtil.validarE2eid(e2eid);
        PixUtil.validarDevolucaoId(id);
        log.debug("Consultando devolução Pix — e2eid: {}, id: {}", e2eid, id);
        return pixApiClient.consultarDevolucao(e2eid, id);
    }

    private String resolverTxid(String txid) {
        if (txid == null || txid.isBlank()) {
            return PixUtil.gerarTxid();
        }
        PixUtil.validarTxid(txid);
        return txid;
    }

    private String resolverPeriodoInicio(String inicio) {
        return (inicio == null || inicio.isBlank()) ? PixUtil.periodoInicioPadrao() : inicio;
    }

    private String resolverPeriodoFim(String fim) {
        return (fim == null || fim.isBlank()) ? PixUtil.periodoFimPadrao() : fim;
    }
}
