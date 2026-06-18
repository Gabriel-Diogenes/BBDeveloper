$ErrorActionPreference = "Continue"
$base = "http://localhost:8080"
$pixKey = "9e881f18-cc66-4fc7-8f2c-a795dbb2bfc1"
$convenio = 3128557
$agencia = "452"
$conta = "123873"

$results = @()

function Invoke-Test {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [string]$Body,
        [int[]]$AcceptStatus = @(200, 201, 204)
    )
    Write-Host "`n===== $Name =====" -ForegroundColor Cyan
    Write-Host "$Method $Url"
    try {
        $params = @{
            Uri             = $Url
            Method          = $Method
            UseBasicParsing = $true
            TimeoutSec      = 90
        }
        if ($Body) {
            $params.Body        = $Body
            $params.ContentType = "application/json"
        }
        $resp = Invoke-WebRequest @params
        $ok = $AcceptStatus -contains $resp.StatusCode
        if ($ok) {
            Write-Host "STATUS: $($resp.StatusCode) OK" -ForegroundColor Green
        } else {
            Write-Host "STATUS: $($resp.StatusCode) UNEXPECTED" -ForegroundColor Yellow
        }
        $content = $resp.Content
        if ($content.Length -gt 500) { $content = $content.Substring(0, 500) + "..." }
        Write-Host "BODY: $content"
        $script:results += [pscustomobject]@{ Name = $Name; Status = $resp.StatusCode; Ok = $ok; Error = $null }
        return $resp.Content
    }
    catch {
        $sc = $null
        if ($_.Exception.Response) { $sc = [int]$_.Exception.Response.StatusCode }
        $msg = $_.ErrorDetails.Message
        if (-not $msg) { $msg = $_.Exception.Message }
        $ok = $AcceptStatus -contains $sc
        if ($ok) {
            Write-Host "STATUS: $sc OK (esperado)" -ForegroundColor Green
        } else {
            Write-Host "STATUS: $sc FAIL" -ForegroundColor Red
        }
        Write-Host "ERROR: $msg"
        $script:results += [pscustomobject]@{ Name = $Name; Status = $sc; Ok = $ok; Error = $msg }
        return $null
    }
}

function New-Txid {
    -join ((1..32) | ForEach-Object { "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".ToCharArray() | Get-Random })
}

$now = Get-Date
$inicio = $now.AddDays(-3).ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ssZ")
$fim = $now.ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ssZ")

# Auth
Invoke-Test -Name "01 Auth" -Method POST -Url "$base/token" | Out-Null

# PIX Cob
$cobBody = "{`"calendario`":{`"expiracao`":3600},`"devedor`":{`"cpf`":`"12345678909`",`"nome`":`"Francisco da Silva`"},`"valor`":{`"original`":`"1.00`"},`"chave`":`"$pixKey`",`"solicitacaoPagador`":`"Teste`"}"
$cobResp = Invoke-Test -Name "02 PIX Cob Criar POST" -Method POST -Url "$base/pix/cob" -Body $cobBody -AcceptStatus @(200, 201)
$txid = $null
if ($cobResp) { try { $txid = ($cobResp | ConvertFrom-Json).txid } catch {} }
if (-not $txid) { $txid = New-Txid; Invoke-Test -Name "02b PIX Cob Criar PUT" -Method PUT -Url "$base/pix/cob/$txid" -Body $cobBody | Out-Null }

if ($txid) {
    Invoke-Test -Name "03 PIX Cob Consultar" -Method GET -Url "$base/pix/cob/$txid" | Out-Null
    Invoke-Test -Name "04 PIX Cob Revisar" -Method PATCH -Url "$base/pix/cob/$txid" -Body '{"valor":{"original":"2.00"}}' | Out-Null
}
Invoke-Test -Name "05 PIX Cob Listar" -Method GET -Url "$base/pix/cob?inicio=$inicio&fim=$fim" | Out-Null
if ($txid) {
    Invoke-Test -Name "06 PIX Cob Cancelar" -Method PATCH -Url "$base/pix/cob/$txid" -Body '{"status":"REMOVIDA_PELO_USUARIO_RECEBEDOR"}' | Out-Null
}

# PIX CobV
$txidCobv = New-Txid
$venc = (Get-Date).AddDays(30).ToString("yyyy-MM-dd")
$cobvBody = "{`"calendario`":{`"dataDeVencimento`":`"$venc`",`"validadeAposVencimento`":30},`"devedor`":{`"cpf`":`"12345678909`",`"nome`":`"Francisco da Silva`",`"logradouro`":`"Rua Exemplo`",`"cidade`":`"Brasilia`",`"uf`":`"DF`",`"cep`":`"70040912`"},`"valor`":{`"original`":`"10.00`"},`"chave`":`"$pixKey`",`"solicitacaoPagador`":`"Teste CobV`"}"
$cobvResp = Invoke-Test -Name "07 PIX CobV Criar PUT" -Method PUT -Url "$base/pix/cobv/$txidCobv" -Body $cobvBody
if ($cobvResp) { try { $txidCobv = ($cobvResp | ConvertFrom-Json).txid } catch {} }
if ($txidCobv) {
    Invoke-Test -Name "08 PIX CobV Consultar" -Method GET -Url "$base/pix/cobv/$txidCobv" | Out-Null
    Invoke-Test -Name "09 PIX CobV Revisar" -Method PATCH -Url "$base/pix/cobv/$txidCobv" -Body '{"valor":{"original":"15.00"}}' | Out-Null
}
Invoke-Test -Name "10 PIX CobV Listar" -Method GET -Url "$base/pix/cobv?inicio=$inicio&fim=$fim" | Out-Null
if ($txidCobv) {
    Invoke-Test -Name "11 PIX CobV Cancelar" -Method PATCH -Url "$base/pix/cobv/$txidCobv" -Body '{"status":"REMOVIDA_PELO_USUARIO_RECEBEDOR"}' | Out-Null
}

# PIX Recebidos
$pixListResp = Invoke-Test -Name "12 PIX Listar Recebidos" -Method GET -Url "$base/pix?inicio=$inicio&fim=$fim" -AcceptStatus @(200)
$e2eid = $null
if ($pixListResp) {
    try {
        $pl = $pixListResp | ConvertFrom-Json
        if ($pl.pix -and $pl.pix.Count -gt 0) { $e2eid = $pl.pix[0].endToEndId }
    } catch {}
}
if ($e2eid) {
    Invoke-Test -Name "13 PIX Consultar Recebido" -Method GET -Url "$base/pix/$e2eid" | Out-Null
    $devId = (New-Txid).Substring(0, 26)
    Invoke-Test -Name "14 PIX Solicitar Devolucao" -Method PUT -Url "$base/pix/$e2eid/devolucao/$devId" -Body '{"valor":"0.01"}' -AcceptStatus @(200, 201, 400)
    Invoke-Test -Name "15 PIX Consultar Devolucao" -Method GET -Url "$base/pix/$e2eid/devolucao/$devId" -AcceptStatus @(200, 404)
} else {
    Write-Host "`n>>> Sem Pix recebidos no periodo - pulando consulta/devolucao (esperado em homolog)" -ForegroundColor Yellow
    $results += [pscustomobject]@{ Name = "13 PIX Consultar Recebido"; Status = "SKIP"; Ok = $true; Error = "sem e2eid" }
    $results += [pscustomobject]@{ Name = "14 PIX Solicitar Devolucao"; Status = "SKIP"; Ok = $true; Error = "sem e2eid" }
    $results += [pscustomobject]@{ Name = "15 PIX Consultar Devolucao"; Status = "SKIP"; Ok = $true; Error = "sem e2eid" }
}

# Cobranca
Invoke-Test -Name "16 Cob Listar Boletos" -Method GET -Url "$base/cobranca/boletos?numeroConvenio=$convenio&agenciaBeneficiario=$agencia&contaBeneficiario=$conta" -AcceptStatus @(200, 502)
$boletoResp = Invoke-Test -Name "17 Cob Registrar Boleto" -Method POST -Url "$base/cobranca/boletos?numeroConvenio=$convenio&nomePagador=Francisco%20da%20Silva&cpfCnpj=12345678909&valor=10.00&diasVencimento=30&comPix=true"
$numeroBoleto = $null
if ($boletoResp) { try { $numeroBoleto = ($boletoResp | ConvertFrom-Json).numero } catch {} }

if ($numeroBoleto) {
    $consultUrl = "$base/cobranca/boletos/$numeroBoleto" + "?numeroConvenio=$convenio"
    Invoke-Test -Name "18 Cob Consultar Boleto" -Method GET -Url $consultUrl | Out-Null
    $novaData = (Get-Date).AddDays(45).ToString("dd.MM.yyyy")
    $altBody = "{`"numeroConvenio`":$convenio,`"indicadorNovaDataVencimento`":`"S`",`"alteracaoData`":{`"novaDataVencimento`":`"$novaData`"}}"
    Invoke-Test -Name "19 Cob Alterar Boleto" -Method PATCH -Url "$base/cobranca/boletos/$numeroBoleto" -Body $altBody -AcceptStatus @(200, 400)
    Invoke-Test -Name "20 Cob Consultar Pix Boleto" -Method GET -Url "$base/cobranca/boletos/$numeroBoleto/pix?numeroConvenio=$convenio" | Out-Null
    Invoke-Test -Name "21 Cob Gerar Pix Boleto" -Method POST -Url "$base/cobranca/boletos/$numeroBoleto/pix?numeroConvenio=$convenio" -AcceptStatus @(200, 400)
    Invoke-Test -Name "22 Cob Cancelar Pix Boleto" -Method DELETE -Url "$base/cobranca/boletos/$numeroBoleto/pix?numeroConvenio=$convenio" -AcceptStatus @(200, 204, 400)
    Invoke-Test -Name "23 Cob Baixar Boleto" -Method POST -Url "$base/cobranca/boletos/$numeroBoleto/baixar?numeroConvenio=$convenio" -AcceptStatus @(200, 400)
    Invoke-Test -Name "24 Cob Cancelar Boleto" -Method POST -Url "$base/cobranca/boletos/$numeroBoleto/cancelar?numeroConvenio=$convenio" -AcceptStatus @(200, 400)
} else {
    Write-Host "`n>>> Boleto nao registrado - pulando testes dependentes" -ForegroundColor Yellow
}

Write-Host "`n`n========== RESUMO ==========" -ForegroundColor Cyan
$failed = $results | Where-Object { -not $_.Ok }
$results | Format-Table -AutoSize
Write-Host "Total: $($results.Count) | OK: $(($results | Where-Object { $_.Ok }).Count) | FAIL: $($failed.Count)" -ForegroundColor $(if ($failed.Count -eq 0) { 'Green' } else { 'Red' })
if ($failed.Count -gt 0) { exit 1 }
