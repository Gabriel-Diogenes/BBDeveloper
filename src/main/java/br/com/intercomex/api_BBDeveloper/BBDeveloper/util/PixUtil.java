package br.com.intercomex.api_BBDeveloper.BBDeveloper.util;

import java.util.UUID;

public class PixUtil {

    public static String gerarTxid() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "");
    }
}
