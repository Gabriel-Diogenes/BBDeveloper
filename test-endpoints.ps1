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

# 4. Cobrança
Invoke-Test -Name "Cobranca - Listar Boletos" -Method GET -Url "$base/cobranca/boletos?numeroConvenio=3128557&agenciaBeneficiario=452&contaBeneficiario=123873" | Out-Null
$boletoResp = Invoke-Test -Name "Cobranca - Registrar Boleto" -Method POST -Url "$base/cobranca/boletos?numeroConvenio=3128557&nomePagador=Francisco%20da%20Silva&cpfCnpj=12345678909&valor=10.00&diasVencimento=30&comPix=true"
$numeroBoleto = $null
if ($boletoResp) { try { $numeroBoleto = ($boletoResp | ConvertFrom-Json).numero } catch {} }
Write-Host "`n>>> numeroBoleto capturado: $numeroBoleto" -ForegroundColor Yellow

if ($numeroBoleto) {
    Invoke-Test -Name "Cobranca - Consultar Pix do Boleto" -Method GET -Url "$base/cobranca/boletos/$numeroBoleto/pix?numeroConvenio=3128557" | Out-Null
}

Write-Host "`n===== FIM DOS TESTES =====" -ForegroundColor Cyan
