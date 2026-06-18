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
$diffLines = $diff -split "`n" | Where-Object {
    $_ -match '^\+' -and
    $_ -notmatch 'application-secrets\.properties\.example|\.env\.example|\.githooks/'
}

$diffContent = $diffLines -join "`n"

if ($diffContent -match '(?im)^\+.*bb\.(homolog|producao)\.(client-secret|ssl-cert-password|developer-key)=[^\s]{8,}') {
    Write-Host ""
    Write-Host "[PRE-COMMIT] BLOQUEADO: credencial BB em arquivo versionado." -ForegroundColor Red
    Write-Host "Mova para application-secrets.properties"
    Write-Host ""
    exit 1
}

if ($diffContent -match '(?im)^\+.*BEGIN (RSA |EC )?PRIVATE KEY') {
    Write-Host ""
    Write-Host "[PRE-COMMIT] BLOQUEADO: chave privada no commit." -ForegroundColor Red
    Write-Host ""
    exit 1
}

exit 0
