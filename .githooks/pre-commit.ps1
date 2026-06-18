$ErrorActionPreference = "Stop"

$blockedFiles = @(
    "application-secrets.properties"
    ".env"
)

$staged = git diff --cached --name-only
if (-not $staged) { exit 0 }

foreach ($file in $staged) {
    foreach ($blocked in $blockedFiles) {
        if ($file -eq $blocked -or $file -like "*/$blocked") {
            Write-Host ""
            Write-Host "[PRE-COMMIT] BLOQUEADO: tentativa de commitar '$file'" -ForegroundColor Red
            Write-Host "Este arquivo contem credenciais e deve permanecer local."
            Write-Host ""
            exit 1
        }
    }
    if ($file -match '\.(pfx|p12|pem|key)$') {
        Write-Host ""
        Write-Host "[PRE-COMMIT] BLOQUEADO: certificado ou chave privada no commit ($file)." -ForegroundColor Red
        Write-Host ""
        exit 1
    }
}

$diff = git diff --cached -U0 --no-color
if ($diff -match '(?im)^\+.*(client-secret|ssl-cert-password|developer-key=|BEGIN (RSA |EC )?PRIVATE KEY)') {
    Write-Host ""
    Write-Host "[PRE-COMMIT] BLOQUEADO: possivel segredo no conteudo do commit." -ForegroundColor Red
    Write-Host "Use application-secrets.properties ou variaveis de ambiente."
    Write-Host ""
    exit 1
}

if ($diff -match '(?m)^\+bb\.(homolog|producao)\.(client-secret|client-id)=[^\s]{20,}') {
    Write-Host ""
    Write-Host "[PRE-COMMIT] BLOQUEADO: credencial BB em arquivo versionado." -ForegroundColor Red
    Write-Host "Mova para application-secrets.properties"
    Write-Host ""
    exit 1
}

exit 0
