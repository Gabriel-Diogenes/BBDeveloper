@echo off
setlocal EnableExtensions

rem Copie este arquivo para C:\api\bbdeveloper\ no servidor de producao.
rem Ajuste APP_DIR, PROFILE e JAVA_HOME conforme o ambiente.

set "APP_DIR=C:\api\bbdeveloper"
set "JAR=BBDeveloper-0.0.1-SNAPSHOT.jar"
set "PROFILE=producao"
set "PORT=8080"

if defined JAVA_HOME (
    set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
) else (
    set "JAVA_EXE=java"
)

echo [%date% %time%] Parando BBDeveloper na porta %PORT%...
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":%PORT%" ^| findstr "LISTENING"') do (
    taskkill /F /PID %%p >nul 2>&1
)

timeout /t 2 /nobreak >nul

cd /d "%APP_DIR%"
if not exist "%JAR%" (
    echo ERRO: JAR nao encontrado em %APP_DIR%\%JAR%
    exit /b 1
)

echo [%date% %time%] Iniciando BBDeveloper (profile=%PROFILE%)...
start "BBDeveloper" "%JAVA_EXE%" -Dspring.profiles.active=%PROFILE% -jar "%JAR%"

echo [%date% %time%] Comando de start enviado. Verifique http://localhost:%PORT%/actuator/health
endlocal
