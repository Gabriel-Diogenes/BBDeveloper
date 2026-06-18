$ErrorActionPreference = "Continue"
$base = "http://localhost:8080"

function Invoke-Test {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [string]$Body
    )
    Write-Host "`n===== $Name =====" -ForegroundColor Cyan
    Write-Host "$Method $Url"
    try {
        $params = @{
            Uri             = $Url
            Method          = $Method
            UseBasicParsing = $true
            TimeoutSec      = 60
        }
        if ($Body) {
            $params.Body        = $Body
            $params.ContentType = "application/json"
        }
        $resp = Invoke-WebRequest @params
        Write-Host "STATUS: $($resp.StatusCode)" -ForegroundColor Green
        Write-Host "BODY: $($resp.Content)"
        return $resp.Content
    }
    catch {
        $sc = $null
        if ($_.Exception.Response) { $sc = [int]$_.Exception.Response.StatusCode }
        Write-Host "STATUS: $sc" -ForegroundColor Red
        $msg = $_.ErrorDetails.Message
        if (-not $msg) { $msg = $_.Exception.Message }
        Write-Host "ERROR: $msg"
        return $null
    }
}

# 1. Auth
Invoke-Test -Name "Auth - Gerar Token" -Method POST -Url "$base/token" | Out-Null

# 2. PIX Cob
$cobBody = '{"calendario":{"expiracao":3600},"devedor":{"cpf":"12345678909","nome":"Francisco da Silva"},"valor":{"original":"1.00"},"chave":"hmtestes2@bb.com.br","solicitacaoPagador":"Pagamento de teste Pix"}'
$cobResp = Invoke-Test -Name "PIX Cob - Criar sem txid (POST)" -Method POST -Url "$base/pix/cob" -Body $cobBody
$txid = $null
if ($cobResp) { try { $txid = ($cobResp | ConvertFrom-Json).txid } catch {} }
Write-Host "`n>>> txid capturado: $txid" -ForegroundColor Yellow

if ($txid) {
    Invoke-Test -Name "PIX Cob - Consultar" -Method GET -Url "$base/pix/cob/$txid" | Out-Null
    Invoke-Test -Name "PIX Cob - Revisar (PATCH)" -Method PATCH -Url "$base/pix/cob/$txid" -Body '{"valor":{"original":"2.00"}}' | Out-Null
}
Invoke-Test -Name "PIX Cob - Listar" -Method GET -Url "$base/pix/cob" | Out-Null
if ($txid) {
    Invoke-Test -Name "PIX Cob - Cancelar (PATCH)" -Method PATCH -Url "$base/pix/cob/$txid" -Body '{"status":"REMOVIDA_PELO_USUARIO_RECEBEDOR"}' | Out-Null
}

# 3. PIX CobV
$cobvBody = '{"calendario":{"dataDeVencimento":"2026-07-15","validadeAposVencimento":30},"devedor":{"cpf":"12345678909","nome":"Francisco da Silva","logradouro":"Rua Exemplo, 100","cidade":"Brasilia","uf":"DF","cep":"70040912"},"valor":{"original":"10.00"},"chave":"hmtestes2@bb.com.br","solicitacaoPagador":"Pagamento de servico prestado"}'
$txidCobv = -join ((1..32) | ForEach-Object { "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".ToCharArray() | Get-Random })
Write-Host "`n>>> txidCobv gerado: $txidCobv" -ForegroundColor Yellow
$cobvResp = Invoke-Test -Name "PIX CobV - Criar (PUT)" -Method PUT -Url "$base/pix/cobv/$txidCobv" -Body $cobvBody
if ($cobvResp) { try { $txidCobv = ($cobvResp | ConvertFrom-Json).txid } catch {} }

if ($txidCobv) {
    Invoke-Test -Name "PIX CobV - Consultar" -Method GET -Url "$base/pix/cobv/$txidCobv" | Out-Null
    Invoke-Test -Name "PIX CobV - Revisar (PATCH)" -Method PATCH -Url "$base/pix/cobv/$txidCobv" -Body '{"valor":{"original":"15.00"}}' | Out-Null
}
Invoke-Test -Name "PIX CobV - Listar" -Method GET -Url "$base/pix/cobv" | Out-Null
if ($txidCobv) {
    Invoke-Test -Name "PIX CobV - Cancelar (PATCH)" -Method PATCH -Url "$base/pix/cobv/$txidCobv" -Body '{"status":"REMOVIDA_PELO_USUARIO_RECEBEDOR"}' | Out-Null
}

# 4. PIX Recebidos
$inicio = (Get-Date).AddDays(-3).ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ssZ")
$fim = (Get-Date).ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ssZ")
$pixListResp = Invoke-Test -Name "PIX Recebidos - Listar" -Method GET -Url "$base/pix?inicio=$inicio&fim=$fim"
$e2eid = $null
if ($pixListResp) { try { $pl = $pixListResp | ConvertFrom-Json; if ($pl.pix -and $pl.pix.Count -gt 0) { $e2eid = $pl.pix[0].endToEndId } } catch {} }
if ($e2eid) {
    Invoke-Test -Name "PIX Recebidos - Consultar por e2eid" -Method GET -Url "$base/pix/$e2eid" | Out-Null
    $devId = -join ((1..26) | ForEach-Object { "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".ToCharArray() | Get-Random })
    Invoke-Test -Name "PIX Devolucao - Solicitar" -Method PUT -Url "$base/pix/$e2eid/devolucao/$devId" -Body '{"valor":"0.01"}' | Out-Null
    Invoke-Test -Name "PIX Devolucao - Consultar" -Method GET -Url "$base/pix/$e2eid/devolucao/$devId" | Out-Null
}

# 5. Cobrança
Invoke-Test -Name "Cobranca - Listar Boletos" -Method GET -Url "$base/cobranca/boletos?numeroConvenio=3128557&agenciaBeneficiario=452&contaBeneficiario=123873" | Out-Null
$dataHoje = Get-Date -Format "dd.MM.yyyy"
$dataVenc = (Get-Date).AddDays(30).ToString("dd.MM.yyyy")
$seq = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds() % 10000000000
$numeroTitulo = "0003128557{0:D10}" -f $seq
$boletoBody = @"
{
  "numeroConvenio": 3128557,
  "numeroCarteira": 17,
  "numeroVariacaoCarteira": 35,
  "codigoModalidade": 1,
  "dataEmissao": "$dataHoje",
  "dataVencimento": "$dataVenc",
  "valorOriginal": 10.00,
  "indicadorPix": "S",
  "numeroTituloCliente": "$numeroTitulo",
  "campoUtilizacaoBeneficiario": "SERVICO PRESTADO",
  "pagador": {
    "tipoInscricao": 1,
    "numeroInscricao": "12345678909",
    "nome": "Francisco da Silva",
    "endereco": "Rua Exemplo 100",
    "cep": "70040912",
    "cidade": "Brasilia",
    "bairro": "Centro",
    "uf": "DF"
  }
}
"@
$boletoResp = Invoke-Test -Name "Cobranca - Registrar Boleto (payload)" -Method POST -Url "$base/cobranca/boletos" -Body $boletoBody
$numeroBoleto = $null
if ($boletoResp) { try { $numeroBoleto = ($boletoResp | ConvertFrom-Json).numero } catch {} }
Write-Host "`n>>> numeroBoleto capturado: $numeroBoleto" -ForegroundColor Yellow

if ($numeroBoleto) {
    $consultUrl = "$base/cobranca/boletos/$numeroBoleto" + "?numeroConvenio=3128557"
    Invoke-Test -Name "Cobranca - Consultar Boleto" -Method GET -Url $consultUrl | Out-Null
    $novaData = (Get-Date).AddDays(45).ToString("dd.MM.yyyy")
    $altBody = "{`"numeroConvenio`":3128557,`"indicadorNovaDataVencimento`":`"S`",`"alteracaoData`":{`"novaDataVencimento`":`"$novaData`"}}"
    Invoke-Test -Name "Cobranca - Alterar Boleto (PATCH)" -Method PATCH -Url "$base/cobranca/boletos/$numeroBoleto" -Body $altBody | Out-Null
    Invoke-Test -Name "Cobranca - Consultar Pix do Boleto" -Method GET -Url "$base/cobranca/boletos/$numeroBoleto/pix?numeroConvenio=3128557" | Out-Null
}

Write-Host "`n===== FIM DOS TESTES =====" -ForegroundColor Cyan
