package com.intercomex.api_bbdeveloper.exception.bb;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BBErrorResponseParser {

    private final ObjectMapper objectMapper;

    public Optional<String> extractCodigo(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return Optional.empty();
        }

        try {
            JsonNode root = objectMapper.readTree(responseBody);

            Optional<String> fromErros = firstText(root.path("erros"), "codigo");
            if (fromErros.isPresent()) {
                return fromErros;
            }

            Optional<String> fromErrors = firstText(root.path("errors"), "code");
            if (fromErrors.isPresent()) {
                return fromErrors;
            }

            if (root.hasNonNull("codigo")) {
                return Optional.of(root.get("codigo").asText());
            }

            if (root.hasNonNull("codigoErro")) {
                return Optional.of(root.get("codigoErro").asText());
            }

            if (root.hasNonNull("errorCode")) {
                return Optional.of(root.get("errorCode").asText());
            }

            if (root.hasNonNull("subErrorCode")) {
                return Optional.of(root.get("subErrorCode").asText());
            }

            return extractFromRfc7807Type(root.path("type"));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private static Optional<String> firstText(JsonNode arrayNode, String fieldName) {
        if (!arrayNode.isArray() || arrayNode.isEmpty()) {
            return Optional.empty();
        }
        JsonNode first = arrayNode.get(0);
        if (first != null && first.hasNonNull(fieldName)) {
            return Optional.of(first.get(fieldName).asText());
        }
        return Optional.empty();
    }

    private static Optional<String> extractFromRfc7807Type(JsonNode typeNode) {
        if (!typeNode.isTextual()) {
            return Optional.empty();
        }
        String type = typeNode.asText();
        int slash = type.lastIndexOf('/');
        if (slash >= 0 && slash < type.length() - 1) {
            return Optional.of(type.substring(slash + 1));
        }
        return Optional.empty();
    }
}
