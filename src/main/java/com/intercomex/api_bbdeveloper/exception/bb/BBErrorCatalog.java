package com.intercomex.api_bbdeveloper.exception.bb;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class BBErrorCatalog {

    private static final String EXTRATO_RESOURCE = "bb-erros/extrato-erros.json";
    private static final String COBRANCA_RESOURCE = "bb-erros/cobranca-erros.json";
    private static final String PIX_RESOURCE = "bb-erros/pix-erros.json";

    private final Map<String, Map<String, BBErrorEntry>> catalogsByApi;

    public BBErrorCatalog(ObjectMapper objectMapper) {
        this.catalogsByApi = Map.of(
                "extrato", loadCatalog(objectMapper, EXTRATO_RESOURCE),
                "cobranca", loadCatalog(objectMapper, COBRANCA_RESOURCE),
                "pix", loadCatalog(objectMapper, PIX_RESOURCE));
    }

    public Optional<BBErrorEntry> find(String api, String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return Optional.empty();
        }

        Map<String, BBErrorEntry> catalog = catalogsByApi.get(normalizeApi(api));
        if (catalog == null || catalog.isEmpty()) {
            return Optional.empty();
        }

        BBErrorEntry entry = catalog.get(codigo);
        if (entry != null) {
            return Optional.of(entry);
        }

        int dotIndex = codigo.indexOf('.');
        if (dotIndex > 0) {
            return Optional.ofNullable(catalog.get(codigo.substring(0, dotIndex)));
        }

        return Optional.ofNullable(catalog.get(codigo + ".1"));
    }

    private static String normalizeApi(String api) {
        if (api == null) {
            return "";
        }
        return api.trim()
                .toLowerCase()
                .replace("ç", "c")
                .replace("ã", "a")
                .replace("á", "a")
                .replace("â", "a");
    }

    private static Map<String, BBErrorEntry> loadCatalog(ObjectMapper objectMapper, String resourcePath) {
        try (InputStream input = new ClassPathResource(resourcePath).getInputStream()) {
            List<BBErrorEntry> entries = objectMapper.readValue(input, new TypeReference<>() {
            });
            Map<String, BBErrorEntry> catalog = new HashMap<>();
            for (BBErrorEntry entry : entries) {
                catalog.put(entry.codigo(), entry);
            }
            log.info("Catálogo de erros BB carregado: {} ({} códigos)", resourcePath, catalog.size());
            return Collections.unmodifiableMap(catalog);
        } catch (IOException ex) {
            log.error("Falha ao carregar catálogo de erros BB: {}", resourcePath, ex);
            return Map.of();
        }
    }
}
