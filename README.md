# api_bbdeveloper

Proxy **Spring Boot** (Java 17) para as APIs do [Portal BB Developers](https://developers.bb.com.br/): **Pix v2**, **Cobranças v2** e **Extratos v2**. A aplicação centraliza autenticação OAuth2, certificado mTLS e chamadas ao Banco do Brasil, expondo endpoints REST simples para consumo por **Oracle APEX** e outros sistemas internos.

---

## Sumário

- [Visão geral](#visão-geral)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Configuração inicial](#configuração-inicial)
- [Execução](#execução)
- [Perfis e ambientes](#perfis-e-ambientes)
- [Endpoints](#endpoints)
- [Postman](#postman)
- [Testes](#testes)
- [Deploy](#deploy)
- [Integração Oracle APEX](#integração-oracle-apex)
- [Tratamento de erros](#tratamento-de-erros)
- [Regras e limitações do BB](#regras-e-limitações-do-bb)
- [Estrutura do projeto](#estrutura-do-projeto)

---

## Visão geral

| Item | Descrição                                                                                                                                             |
|------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Stack** | Java 17, Spring Boot 4.0.6, WebClient (Reactor Netty)                                                                                                 |
| **Porta padrão** | `8085`                                                                                                                                                |
| **Autenticação local** | Não é necessário enviar `Authorization` — o token OAuth2 é obtido internamente e **reutilizado em cache** (~9 min, margem de 60s antes do vencimento) |
| **Ambientes** | `homologacao` (padrão) e `producao`                                                                                                                   |
| **Escopos OAuth** | `pix.read`, `pix.write`, `cob.read`, `cob.write`, `cobv.read`, `cobv.write`, `cobrancas.boletos-info`, `cobrancas.boletos-requisicao`, `extrato-info` |

### O que a API faz

- **Pix** — cobrança imediata (Cob), cobrança com vencimento (CobV), Pix recebidos (conciliação) e devoluções
- **Cobrança** — registro, consulta, alteração, baixa/cancelamento de boletos e Pix vinculado ao boleto
- **Extrato** — consulta de extrato de conta corrente (mTLS)
- **Utilitários** — geração de token OAuth (`POST /token`) e diagnóstico de ambiente (`GET /ambiente`)

### O que **não** está nesta API

- **Webhook Pix** — será implementado separadamente no **Oracle APEX** (notificações de pagamento em tempo real)

---

## Arquitetura

```
Cliente (APEX / Postman / outro)
        │
        ▼
   Controller  ──►  Service  ──►  ApiClient  ──►  API BB Developers
        │                              │
        │                              ├── bbWebClient (HTTP)
        │                              └── bbMtlsWebClient (mTLS: Pix produção, Extrato)
        ▼
 GlobalExceptionHandler  ──►  BBErrorResolver (catálogo bb-erros/*.json)
```

| Camada | Responsabilidade |
|--------|------------------|
| `controller` | Endpoints REST locais, validação de parâmetros |
| `service` | Regras de negócio, obtenção de token, defaults |
| `client` | Chamadas HTTP ao BB (`PixApiClient`, `CobrancaApiClient`, `ExtratoApiClient`, `BBOAuthClient`) |
| `dto` | Contratos de request/response (espelham APIs BB/BACEN) |
| `properties` | URLs, credenciais e flags por perfil (`BBApiProperties`, `BBCredentials`) |
| `exception/bb` | Catálogos de erros BB, parser da resposta e resolução para mensagens ao ERP |

---

## Pré-requisitos

- **JDK 17**
- **Maven** (ou use `./mvnw`)
- Credenciais no [Portal BB Developers](https://developers.bb.com.br/) (Client ID, Client Secret, Developer Application Key)
- Certificado **PFX** (`.pfx`) para mTLS:
  - **Homologação:** extrato e OAuth
  - **Produção:** Pix, extrato e OAuth
- Chave Pix registrada na conta vinculada ao app (obrigatória para criar Cob/CobV)

---

## Configuração inicial

### 1. Segredos

```powershell
.\scripts\setup-secrets.ps1
```

Isso copia `src/main/resources/application-secrets.properties.example` para `application-secrets.properties`. Preencha com suas credenciais:

```properties
bb.homolog.client-id=...
bb.homolog.client-secret=...
bb.homolog.developer-key=...
bb.homolog.ssl-cert-path=certificados/bb-certificado.pfx
bb.homolog.ssl-cert-password=...
bb.homolog.pix-key=...

bb.producao.client-id=...
# ... (mesma estrutura para produção)
```

O arquivo `application-secrets.properties` está no `.gitignore` e **nunca** deve ser commitado.

Alternativa em produção: variáveis de ambiente (`BB_HOMOLOG_CLIENT_ID`, `BB_PRODUCAO_CLIENT_SECRET`, etc.) — ver comentários no `.example`.

### 2. Certificados

Coloque os arquivos `.pfx` em:

```
src/main/resources/certificados/
```

(Pasta ignorada pelo Git.)

### 3. Pre-commit hook (recomendado)

Impede commit acidental de segredos:

```bash
git config core.hooksPath .githooks
```

---

## Execução

### Desenvolvimento (Maven / IntelliJ)

```bash
./mvnw spring-boot:run
```

A aplicação sobe na porta **8085** (`server.port` em `application.properties`). No IntelliJ, execute a classe principal sem sobrescrever a porta em *VM options* (evite `-Dserver.port=8080`).

Com perfil explícito:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=homologacao
./mvnw spring-boot:run -Dspring-boot.run.profiles=producao
```

### JAR (produção / homologação)

```bash
./mvnw clean package -DskipTests
java "-Dspring.profiles.active=homologacao" -jar target/api_bbdeveloper-0.0.1-SNAPSHOT.jar
java "-Dspring.profiles.active=producao"    -jar target/api_bbdeveloper-0.0.1-SNAPSHOT.jar
```

### Verificar ambiente ativo

```http
GET http://localhost:8085/ambiente
```

Retorna perfil, URLs das APIs BB, se mTLS está habilitado para Pix e se o header de homologação do extrato está configurado.

---

## Perfis e ambientes

| Configuração | Homologação | Produção |
|--------------|-------------|----------|
| Profile Spring | `homologacao` | `producao` |
| OAuth | `https://oauth.hm.bb.com.br/oauth/token` | `https://oauth.bb.com.br/oauth/token` |
| Pix | `https://api.hm.bb.com.br/pix/v2` | `https://api-pix.bb.com.br/pix/v2` |
| Pix mTLS | Não | **Sim** |
| Cobrança | `https://api.hm.bb.com.br/cobrancas/v2` | `https://api.bb.com.br/cobrancas/v2` |
| Extrato | `https://extratos.mtls.api.hm.bb.com.br/v2` | `https://extratos.mtls.api.bb.com.br/v2` |
| Extrato mTLS | Sim | Sim |
| Header extrato homolog | `bb.extrato-homologacao-header` (MCI) | — |

Arquivos de perfil:

- `application.properties` — porta (`8085`), configuração pública e import de secrets
- `application-homologacao.properties`
- `application-producao.properties`
- `application-secrets.properties` — credenciais (local, gitignored)

---

## Endpoints

Base URL local: `http://localhost:8085`

Não envie header `Authorization` nas requisições locais — o token é gerado internamente.

### Autenticação

| Método | Path | Descrição |
|--------|------|-----------|
| `POST` | `/token` | Gera token OAuth2 (opcional; útil para validar credenciais). O mesmo token em cache é usado nas demais chamadas. |

### Pix — Cobrança imediata (Cob)

| Método | Path local | API BB |
|--------|------------|--------|
| `PUT` | `/pix/cob/{txid}` | `PUT /cob/{txid}` |
| `POST` | `/pix/cob` | `POST /cob` |
| `GET` | `/pix/cob/{txid}` | `GET /cob/{txid}` |
| `PATCH` | `/pix/cob/{txid}` | `PATCH /cob/{txid}` |
| `GET` | `/pix/cob` | `GET /cob` |

**Query params (listagem):** `inicio`, `fim`, `cpf`, `cnpj`, `status`, `paginaAtual`, `itensPorPagina`  
`inicio`/`fim` opcionais — padrão: últimos 3 dias. Intervalo máximo: **4 dias**.

**Cancelar cobrança:** `PATCH` com body `{"status":"REMOVIDA_PELO_USUARIO_RECEBEDOR"}` (não existe `DELETE` na API Pix v2).

### Pix — Cobrança com vencimento (CobV)

| Método | Path local | API BB |
|--------|------------|--------|
| `PUT` | `/pix/cobv/{txid}` | `PUT /cobv/{txid}` |
| `GET` | `/pix/cobv/{txid}` | `GET /cobv/{txid}` |
| `PATCH` | `/pix/cobv/{txid}` | `PATCH /cobv/{txid}` |
| `GET` | `/pix/cobv` | `GET /cobv` |

### Pix — Recebidos e devoluções (conciliação)

| Método | Path local | API BB |
|--------|------------|--------|
| `GET` | `/pix` | `GET /pix` |
| `GET` | `/pix/{e2eid}` | `GET /pix/{e2eid}` |
| `PUT` | `/pix/{e2eid}/devolucao/{id}` | `PUT /pix/{e2eid}/devolucao/{id}` |
| `GET` | `/pix/{e2eid}/devolucao/{id}` | `GET /pix/{e2eid}/devolucao/{id}` |

**`e2eid`:** 32 caracteres, iniciando com `E` (padrão EndToEndId).  
**Devolução:** body `{"valor":"10.00"}` — `id` da devolução é gerado pelo cliente (1–35 caracteres alfanuméricos).

### Cobrança — Boletos

| Método | Path local | API BB | Observação |
|--------|------------|--------|------------|
| `GET` | `/cobranca/boletos` | `GET /boletos` | Query: `numeroConvenio`, `agenciaBeneficiario`, `contaBeneficiario` |
| `POST` | `/cobranca/boletos` | `POST /boletos` | Body JSON: `BoletoRegistrarRequestDTO` (payload completo BB). Retorna **201 Created**. |
| `POST` | `/cobranca/boletos/simplificado` | `POST /boletos` | Query: `numeroConvenio`, `nomePagador`, `cpfCnpj`, `valor`, `diasVencimento`, `comPix` — monta o payload internamente |
| `GET` | `/cobranca/boletos/{numeroBoleto}` | `GET /boletos/{id}` | Query: `numeroConvenio` |
| `PATCH` | `/cobranca/boletos/{numeroBoleto}` | `PATCH /boletos/{id}` | Body: `BoletoAlterarRequestDTO` (inclui `numeroConvenio`) |
| `POST` | `/cobranca/boletos/{numeroBoleto}/baixar` | `POST /boletos/{id}/baixar` | Query: `numeroConvenio` |
| `POST` | `/cobranca/boletos/{numeroBoleto}/cancelar` | `POST /boletos/{id}/baixar` | Alias local — BB usa `/baixar` para cancelamento |
| `GET` | `/cobranca/boletos/{numeroBoleto}/pix` | `GET /boletos/{id}/pix` | Query: `numeroConvenio` |
| `POST` | `/cobranca/boletos/{numeroBoleto}/pix` | `POST /boletos/{id}/gerar-pix` | Query: `numeroConvenio` |
| `DELETE` | `/cobranca/boletos/{numeroBoleto}/pix` | `POST /boletos/{id}/cancelar-pix` | Query: `numeroConvenio` |

> **Registro de boleto:** `POST /cobranca/boletos` aceita o JSON completo da API Cobranças v2 do BB (`BoletoRegistrarRequestDTO`). Campos omitidos recebem defaults no serviço (carteira 17, variação 35, duplicata mercantil, etc.). Use `POST /cobranca/boletos/simplificado` para o atalho com query params.

> **Consulta vs registro:** `GET /boletos/{id}` retorna campos diferentes do `POST` (ex.: `codigoLinhaDigitavel`, `dataVencimentoTituloCobranca`). A API local usa `BoletoConsultaResponseDTO` na consulta e `BoletoResponseDTO` no registro.

### Extrato

| Método | Path local | API BB |
|--------|------------|--------|
| `GET` | `/extrato` | `GET /conta-corrente/agencia/{agencia}/conta/{conta}` |

**Query obrigatórios:** `agencia`, `conta`, `dataInicio`, `dataFim`  
**Formatos de data aceitos:** `dd.MM.yyyy`, `dd/MM/yyyy`, `yyyy-MM-dd` ou `DDMMAAAA` (convertidos internamente para o padrão BB)  
**Query opcionais:** `pagina`, `quantidadePorPagina` (BB: mín. 30, máx. 120; omitir = 120)

Agência e conta devem conter apenas números (pontos, espaços e hífen são aceitos como separadores; letras são rejeitadas antes da chamada ao BB).

**Homologação (massa de teste BB):** agência `1505`, conta `1348`, datas `19.04.2023` a `23.04.2023`.

---

## Postman

Importe a collection e, opcionalmente, um environment:

```
postman/api_bbdeveloper.postman_collection.json
postman/api_bbdeveloper.local.postman_environment.json
```

A collection cobre todos os endpoints locais, com variáveis automáticas (`txid`, `numeroBoleto`, `e2eid`, período de listagem Pix, etc.).

---

## Testes

### Testes unitários / integração Spring

```bash
./mvnw clean test
```

### Testes manuais contra API local (homologação)

Com a aplicação rodando em `homologacao`:

```powershell
# Bateria completa (24 endpoints, exceto extrato opcional)
powershell -ExecutionPolicy Bypass -File test-homolog-all.ps1

# Subconjunto rápido
powershell -ExecutionPolicy Bypass -File test-endpoints.ps1
```

---

## Deploy

### Build

```bash
./mvnw clean package -DskipTests
```

Artefato: `target/api_bbdeveloper-0.0.1-SNAPSHOT.jar`

### Servidor

1. Copie o JAR para `C:\api\api_bbdeveloper\`
2. Configure secrets em `config/application-secrets.properties` (modelo em `deploy/config/application-secrets.properties.example`)
3. Coloque o certificado PFX no caminho indicado nas secrets
4. Copie `deploy/executa-api_bbdeveloper.bat` para `C:\api\api_bbdeveloper\` e execute no servidor (o script usa a porta **8085** para parar/iniciar)

Ou manualmente:

```bash
java "-Dspring.profiles.active=producao" -jar api_bbdeveloper-0.0.1-SNAPSHOT.jar
```

Verifique saúde: `GET http://localhost:8085/actuator/health`

### Logs (Log4j2 + Grafana Loki)

Mesmo padrão dos robôs (`TRACK_CNTR`): logs no console e enviados ao Loki via `log4j2.xml`.

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `LOKI_HOST` | `172.19.132.6` | Host do Loki |
| `LOKI_PORT` | `3100` | Porta do Loki |
| `LOKI_VM` | `W7-1` | Label `VM` no Grafana |
| `LOG_LEVEL` | `INFO` | Nível mínimo enviado ao Loki |

No Grafana, filtre por `application="api_bbdeveloper"`.

### Checklist produção

- [ ] Profile `producao` ativo
- [ ] Credenciais de produção preenchidas
- [ ] Certificado PFX de produção válido e liberado no portal BB
- [ ] Chave Pix de produção registrada na conta
- [ ] Convênio de cobrança e extrato habilitados no app BB
- [ ] Firewall liberado para `oauth.bb.com.br`, `api-pix.bb.com.br`, `api.bb.com.br`, `extratos.mtls.api.bb.com.br`

---

## Integração Oracle APEX

Esta API foi pensada como **backend HTTP** para o APEX:

1. Configure um **Web Source Module** ou **REST Data Source** apontando para a URL do api_bbdeveloper (ex.: `http://servidor:8085` ou a porta NAT externa configurada no firewall)
2. **Não** configure OAuth no APEX — a autenticação com o BB é interna
3. Consuma os endpoints conforme a necessidade (Pix recebidos para conciliação, boletos, extrato, etc.)

### Webhook Pix

Notificações de pagamento Pix (**webhook**) **não** são tratadas nesta API. O recebimento de callbacks do BB será implementado **diretamente no Oracle APEX** (endpoint público HTTPS configurado no portal BB Developers).

Para conciliação sem webhook, use:

- `GET /pix` — listar Pix recebidos por período
- `GET /pix/{e2eid}` — detalhe de um pagamento

---

## Tratamento de erros

Respostas de erro seguem um padrão único para facilitar a exibição no ERP:

```json
{
  "erro": "5738445.1",
  "descricao": "A data final informada é superior a data atual. Refaça a requisição ajustando a data."
}
```

| Situação | HTTP | Campo `erro` | Campo `descricao` |
|----------|------|--------------|-------------------|
| Validação local (txid, e2eid, data, agência, conta, etc.) | `400` | `VALIDACAO` | Mensagem explicativa da validação |
| Erro conhecido do BB (código no catálogo) | mesmo status do BB | Código BB (ex.: `5738445.1`, `4678420`, `AcessoNegado`) | Descrição amigável do catálogo |
| Erro do BB sem entrada no catálogo | mesmo status do BB | Código extraído da resposta ou `DESCONHECIDO` | Mensagem padrão de erro desconhecido |

O status HTTP original do Banco do Brasil (400, 403, 404, 500, 502, etc.) é **sempre repassado** na resposta.

### Catálogos de erros

Os códigos e descrições ficam em arquivos JSON por API (editáveis sem recompilar):

| Arquivo | API |
|---------|-----|
| `src/main/resources/bb-erros/extrato-erros.json` | Extrato |
| `src/main/resources/bb-erros/cobranca-erros.json` | Cobrança |
| `src/main/resources/bb-erros/pix-erros.json` | Pix |

Fluxo: ao receber erro do BB, a API extrai o código do body (`erros[].codigo`, `errorCode`, `subErrorCode` ou tipo RFC 7807 no Pix), consulta o catálogo correspondente e retorna `erro` + `descricao`. Novos códigos podem ser incluídos nos JSONs conforme a documentação do portal BB Developers.

### Exemplos

Validação de data inválida (antes de chamar o BB):

```json
{
  "erro": "VALIDACAO",
  "descricao": "Data inválida: '10.06.2026fd'. Use dd.MM.yyyy, dd/MM/yyyy, yyyy-MM-dd ou DDMMAAAA."
}
```

Erro de extrato retornado pelo BB:

```json
{
  "erro": "5738678.1",
  "descricao": "O período entre a data inicial e a data final é superior a 31 dias."
}
```

---

## Regras e limitações do BB

| Regra | Detalhe |
|-------|---------|
| **txid** | 26–35 caracteres `[a-zA-Z0-9]` |
| **e2eid** | 32 caracteres, inicia com `E` |
| **Listagem Pix** | Intervalo `inicio`–`fim` &lt; 5 dias |
| **Chave Pix** | Deve estar registrada na conta do app; senão BB retorna 400 (código 305) |
| **Boleto — 30 min** | Alterar, baixar ou cancelar boleto recém-registrado pode falhar até ~30 min após o registro |
| **Cancelar boleto** | BB usa `POST /boletos/{id}/baixar` (irreversível) |
| **Extrato homolog** | Usar conta/datas de massa de teste do portal BB |
| **Pix produção** | Exige mTLS (`bb.pix-requer-mtls=true`) |

---

## Estrutura do projeto

```
api_bbdeveloper/
├── src/main/java/.../api_bbdeveloper/
│   ├── ApiBbdeveloperApplication.java
│   ├── client/          # PixApiClient, CobrancaApiClient, ExtratoApiClient, BBOAuthClient
│   ├── config/          # WebClientConfig (plain + mTLS)
│   ├── controller/      # REST endpoints
│   ├── dto/             # Request/response por domínio (pix, cobranca, extrato, auth)
│   ├── exception/       # BBApiException, GlobalExceptionHandler, catálogo bb/ (BBErrorCatalog, BBErrorResolver)
│   ├── properties/      # BBApiProperties, BBCredentials
│   ├── service/         # Lógica de negócio
│   └── util/            # PixUtil, ExtratoUtil
├── src/main/resources/
│   ├── application.properties
│   ├── application-homologacao.properties
│   ├── application-producao.properties
│   ├── application-secrets.properties.example
│   ├── bb-erros/        # Catálogos de código/descrição (extrato, cobranca, pix)
│   └── certificados/    # PFX (gitignored)
├── postman/             # Collection e environments
├── scripts/             # setup-secrets.ps1
├── deploy/config/       # Template secrets para servidor
├── test-endpoints.ps1
├── test-homolog-all.ps1
└── pom.xml
```

---

## Licença

Uso interno — Intercomex / projeto api_bbdeveloper.
