package br.com.intercomex.api_BBDeveloper.BBDeveloper.controller;

import br.com.intercomex.api_BBDeveloper.BBDeveloper.properties.BBApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class ConfigController {

    private final BBApiProperties properties;

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getConfigInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("oauthUrl", properties.getOauthUrl());
        info.put("pixBaseUrl", properties.getPixBaseUrl());
        info.put("scope", properties.getScope());
        info.put("clientId", properties.getClientId() != null ? properties.getClientId().substring(0, 10) + "..." : "não configurado");
        info.put("developerKey", properties.getDeveloperKey() != null ? properties.getDeveloperKey().substring(0, 10) + "..." : "não configurado");
        
        return ResponseEntity.ok(info);
    }
}
