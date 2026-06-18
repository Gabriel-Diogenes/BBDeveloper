$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot

$example = Join-Path $root "src\main\resources\application-secrets.properties.example"
$secrets = Join-Path $root "src\main\resources\application-secrets.properties"

if (-not (Test-Path $secrets)) {
    Copy-Item $example $secrets
    Write-Host "Criado: application-secrets.properties (preencha com credenciais do portal BB)" -ForegroundColor Yellow
} else {
    Write-Host "OK: application-secrets.properties ja existe" -ForegroundColor Green
}

Write-Host ""
Write-Host "Para ativar o pre-commit hook, execute no repositorio:" -ForegroundColor Cyan
Write-Host "  git config core.hooksPath .githooks" -ForegroundColor White
Write-Host ""
