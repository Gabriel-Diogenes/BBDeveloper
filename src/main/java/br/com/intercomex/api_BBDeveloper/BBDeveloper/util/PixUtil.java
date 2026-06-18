package br.com.intercomex.api_BBDeveloper.BBDeveloper.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.regex.Pattern;

public class PixUtil {

    private static final Pattern TXID_PATTERN = Pattern.compile("^[a-zA-Z0-9]{26,35}$");
    private static final Pattern E2EID_PATTERN = Pattern.compile("^E[a-zA-Z0-9]{31}$");
    private static final Pattern DEVOLUCAO_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9]{1,35}$");
    private static final int PERIODO_MAXIMO_LISTAGEM_DIAS = 4;

    private PixUtil() {
    }

    public static String gerarTxid() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "");
    }

    public static void validarTxid(String txid) {
        if (txid == null || !TXID_PATTERN.matcher(txid).matches()) {
            throw new IllegalArgumentException(
                    "txid inválido: deve conter de 26 a 35 caracteres alfanuméricos [a-zA-Z0-9]. " +
                            "Exemplo: " + gerarTxid());
        }
    }

    public static void validarE2eid(String e2eid) {
        if (e2eid == null || !E2EID_PATTERN.matcher(e2eid).matches()) {
            throw new IllegalArgumentException(
                    "e2eid inválido: deve conter 32 caracteres alfanuméricos iniciando com E.");
        }
    }

    public static void validarDevolucaoId(String id) {
        if (id == null || !DEVOLUCAO_ID_PATTERN.matcher(id).matches()) {
            throw new IllegalArgumentException(
                    "id de devolução inválido: deve conter de 1 a 35 caracteres alfanuméricos [a-zA-Z0-9].");
        }
    }

    public static void validarPeriodoListagem(String inicio, String fim) {
        if (inicio == null || inicio.isBlank() || fim == null || fim.isBlank()) {
            throw new IllegalArgumentException("Parâmetros 'inicio' e 'fim' são obrigatórios (formato ISO-8601).");
        }

        Instant inicioInstant = Instant.parse(inicio);
        Instant fimInstant = Instant.parse(fim);

        if (fimInstant.isBefore(inicioInstant)) {
            throw new IllegalArgumentException("'fim' deve ser igual ou posterior a 'inicio'.");
        }

        long dias = ChronoUnit.DAYS.between(inicioInstant, fimInstant);
        if (dias >= PERIODO_MAXIMO_LISTAGEM_DIAS + 1) {
            throw new IllegalArgumentException(
                    "O intervalo entre 'inicio' e 'fim' deve ser menor que 5 dias (máximo 4 dias). " +
                            "Exemplo: inicio=2026-06-01T00:00:00Z&fim=2026-06-04T23:59:59Z");
        }
    }

    public static String periodoInicioPadrao() {
        return Instant.now().minus(3, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS).toString();
    }

    public static String periodoFimPadrao() {
        return Instant.now().truncatedTo(ChronoUnit.SECONDS).toString();
    }
}
