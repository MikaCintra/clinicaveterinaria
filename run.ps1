# Script para executar a aplicação da clínica
cd "c:\Users\cintr\OneDrive\Área de Trabalho\clinica-jdbc"

Write-Host "Iniciando Clinica Veterinaria..." -ForegroundColor Green
Write-Host "Aguarde a janela abrir..." -ForegroundColor Yellow

mvn exec:java '-Dexec.mainClass=com.example.clinica.ClinicaApp'
