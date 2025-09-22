<#
ci_local.ps1

Simula os passos principais da pipeline GitHub Actions localmente.

Pré-requisitos:
- Docker desktop rodando
- Java 21 + Maven (para build local) ou use .\mvnw que já existe no repo
- PowerShell (este script foi escrito para Windows PowerShell / PowerShell Core)

Uso básico:
.\scripts\ci_local.ps1            # roda tudo (Postgres, build, start, ZAP, SCA)
.\scripts\ci_local.ps1 -SkipZAP   # pula o ZAP
.\scripts\ci_local.ps1 -SkipSCA   # pula o SCA (Dependency-Check)
.\scripts\ci_local.ps1 -KeepContainers  # não remove container Postgres no fim

#>

param(
    [switch]$SkipZAP,
    [switch]$SkipSCA,
    [switch]$KeepContainers,
    [string]$PostgresImage = 'postgres:15',
    [string]$PostgresUser = 'rodrigoviana',
    [string]$PostgresPassword = 'dfr16464',  # Note: In production, use SecureString or PSCredential for sensitive data
    [string]$PostgresDb = 'challenge2025',
    [int]$PgPortHost = 5432
)

Set-StrictMode -Version Latest

function Write-Info($msg) { Write-Host "[INFO] $msg" -ForegroundColor Cyan }
function Write-ErrorAndExit($msg) { Write-Host "[ERROR] $msg" -ForegroundColor Red; exit 1 }

$RepoRoot = Resolve-Path "$PSScriptRoot\.."
Write-Info "Repo root: $RepoRoot"

Write-Info "Checking Docker availability..."
try {
    & docker info > $null 2>&1
} catch {
    Write-ErrorAndExit "Docker não está acessível. Inicie o Docker Desktop antes de rodar este script."
}

# Start Postgres container
$containerName = 'gamblers-postgres'
$existing = (& docker ps -a --filter "name=$containerName" --format "{{.Names}}") -contains $containerName
if ($existing) {
    Write-Info "Removing existing container $containerName"
    docker rm -f $containerName | Out-Null
}

Write-Info "Starting Postgres container ($PostgresImage)"
docker run -d --name $containerName -e POSTGRES_USER=$PostgresUser -e POSTGRES_PASSWORD=$PostgresPassword -e POSTGRES_DB=$PostgresDb -p $PgPortHost:5432 $PostgresImage | Out-Null

Write-Info "Waiting for Postgres to be ready..."
$ready = $false
for ($i=0; $i -lt 60; $i++) {
    try {
        docker exec $containerName pg_isready -U $PostgresUser -d $PostgresDb 2>&1 | Out-Null
        if ($LASTEXITCODE -eq 0) { $ready = $true; break }
    } catch {
        # ignore
    }
    Start-Sleep -Seconds 2
}
if (-not $ready) { Write-ErrorAndExit "Postgres não ficou pronto em tempo." }
Write-Info "Postgres pronto"

# Build project
Write-Info "Building project with Maven (skip tests)"
Push-Location $RepoRoot
if (Test-Path '.\mvnw') {
    & .\mvnw -B clean package -DskipTests
    if ($LASTEXITCODE -ne 0) { Write-ErrorAndExit "Maven build falhou" }
} else {
    & mvn -B clean package -DskipTests
    if ($LASTEXITCODE -ne 0) { Write-ErrorAndExit "Maven build falhou" }
}
Pop-Location

# Locate the built jar
$targetDir = Join-Path $RepoRoot 'target'
$jar = Get-ChildItem -Path $targetDir -Filter '*.jar' | Where-Object { $_.Name -notlike '*sources*' } | Select-Object -First 1
if (-not $jar) { Write-ErrorAndExit "JAR não encontrado em $targetDir" }
$jarPath = $jar.FullName
Write-Info "Found JAR: $jarPath"

# Start the application
Write-Info "Starting application (logging to app.log)"
$appLog = Join-Path $RepoRoot 'app.log'
if (Test-Path $appLog) { Remove-Item $appLog -Force }

try {
    # Start in background and redirect output
    Start-Process -FilePath 'java' -ArgumentList '-jar', "`"$jarPath`"" -WorkingDirectory $RepoRoot -NoNewWindow -WindowStyle Hidden -RedirectStandardOutput $appLog -RedirectStandardError $appLog -PassThru | Out-Null
} catch {
    Write-ErrorAndExit "Falha ao iniciar a aplicação: $_"
}

# Wait for actuator health
Write-Info "Waiting for /actuator/health to return UP..."
$healthy = $false
for ($i=0; $i -lt 60; $i++) {
    try {
        $resp = Invoke-RestMethod -Uri 'http://localhost:8080/actuator/health' -UseBasicParsing -ErrorAction Stop
        if ($resp.status -eq 'UP') { $healthy = $true; break }
    } catch {
        # fallback: read app log to help debugging
    }
    Start-Sleep -Seconds 2
}
if (-not $healthy) {
    Write-Host "--- app.log ---"; Get-Content $appLog -Tail 200 | ForEach-Object { Write-Host $_ }
    Write-ErrorAndExit "Aplicação não ficou saudável em tempo (ver app.log)"
}
Write-Info "Aplicação saudável"

# Create reports dir
$reportsDir = Join-Path $RepoRoot 'local-ci-reports'
if (-not (Test-Path $reportsDir)) { New-Item -ItemType Directory -Path $reportsDir | Out-Null }

# Run ZAP (if not skipped)
if (-not $SkipZAP) {
    Write-Info "Running OWASP ZAP baseline scan"
    # Ensure reports folder for zap
    $zapOut = Join-Path $reportsDir 'zap'
    if (-not (Test-Path $zapOut)) { New-Item -ItemType Directory -Path $zapOut | Out-Null }

    # On Windows Docker, prefer host.docker.internal
    $zapCmd = "zap-baseline.py -t http://host.docker.internal:8080 -J /zap/wrk/report_json.json -w /zap/wrk/report_md.md -r /zap/wrk/report_html.html"

    Write-Info "Pulling ZAP image..."
    docker pull ghcr.io/zaproxy/zaproxy:stable | Out-Null

    Write-Info "Running ZAP (may take a few minutes)"
    docker run --rm -v "${RepoRoot}:/zap/wrk/:rw" --add-host=host.docker.internal:host-gateway -t ghcr.io/zaproxy/zaproxy:stable $zapCmd

    # move reports
    if (Test-Path (Join-Path $RepoRoot 'report_html.html')) {
        Move-Item -Path (Join-Path $RepoRoot 'report_html.html') -Destination (Join-Path $zapOut 'report.html') -Force
    }
    if (Test-Path (Join-Path $RepoRoot 'report_json.json')) { Move-Item (Join-Path $RepoRoot 'report_json.json') (Join-Path $zapOut 'report.json') -Force }
    if (Test-Path (Join-Path $RepoRoot 'report_md.md')) { Move-Item (Join-Path $RepoRoot 'report_md.md') (Join-Path $zapOut 'report.md') -Force }
}

# Run Dependency-Check via Docker (if not skipped)
if (-not $SkipSCA) {
    Write-Info "Running Dependency-Check (this may take some time)"
    $scaOut = Join-Path $reportsDir 'sca'
    if (-not (Test-Path $scaOut)) { New-Item -ItemType Directory -Path $scaOut | Out-Null }

    # If you have NVD_API_KEY, set it as env var before running this script to speed updates
    $envOpt = @()
    if ($env:NVD_API_KEY) { $envOpt += "-e NVD_API_KEY=$env:NVD_API_KEY" }

    # Run owasp dependency-check Docker image
    $dockerArgs = @('--rm','-v',"${RepoRoot}:/src",'-v',"${scaOut}:/report",'owasp/dependency-check:latest','--project','GamblersApp','--scan','/src','--format','HTML','--out','/report','--failOnCVSS','7.0')
    docker run @dockerArgs
}

Write-Info "Local CI finished. Reports collected under: $reportsDir"

if (-not $KeepContainers) {
    Write-Info "Stopping and removing Postgres container"
    docker rm -f $containerName | Out-Null
}

Write-Info "Done"
