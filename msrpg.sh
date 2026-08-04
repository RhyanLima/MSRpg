#!/usr/bin/env bash

set -eou pipefail

PROJECT_NAME="MSRpg"

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$ROOT_DIR/backend"
FRONTEND_DIR="$ROOT_DIR/frontend"

MAIN_CLASS="com.rcl.msrpg.App"
BACKEND_JAR="$BACKEND_DIR/target/msrpg-backend.jar"

DEFAULT_FRONTEND_PORT="4200"

BACKEND_PID=""
FRONTEND_PID=""

print_help() {
  cat << EOF
$PROJECT_NAME - Script de desenvolvimento

Uso:
  ./msrpg.sh <comando> [opções]

Comandos:
  backend              Roda somente o backend via Maven exec:java (Padrão)
  backend:jar          Roda somente o backend usando o JAR gerado
  backend:build        Compila somente o backend
  backend:test         Roda os testes do backend

  frontend             Roda somente o frontend Angular
  frontend:build       Compila somente o frontend Angular
  frontend:test        Roda os testes do frontend

  dev                  Roda backend + frontend juntos
  electron             Roda Electron em modo dev
  electron:dist        Gera pacote desktop com electron-builder

  build                Compila backend + frontend
  install              Instala dependências npm da raiz e do frontend
  clean                Remove artefatos de build

  help, -h, --help     Mostra esta ajuda

Opções:
  --front-port PORT    Porta do frontend Angular. Padrão: $DEFAULT_FRONTEND_PORT
  --prod               Usa modo produção para o build do frontend
  --skip-build         Pula build antes do electron/electron:dist

Exemplos:
  ./msrpg.sh dev
  ./msrpg.sh backend
  ./msrpg.sh build --prod
EOF
}

log() {
  echo "[$PROJECT_NAME] $*"
}

fail() {
  echo "[$PROJECT_NAME][ERRO] $*" >&2
  exit 1
}

command_exists() {
  command -v "$1" >/dev/null 2>&1
}

require_command() {
  local cmd="$1"
  if ! command_exists "$cmd"; then
    fail "Comando não encontrado: $cmd"
  fi
}

ensure_backend_dir() {
  [[ -d "$BACKEND_DIR" ]] || fail "Diretório backend não encontrado: $BACKEND_DIR"
  [[ -f "$BACKEND_DIR/pom.xml" ]] || fail "pom.xml não encontrado em: $BACKEND_DIR"
}

ensure_frontend_dir() {
  [[ -d "$FRONTEND_DIR" ]] || fail "Diretório frontend não encontrado: $FRONTEND_DIR"
  [[ -f "$FRONTEND_DIR/package.json" ]] || fail "package.json não encontrado em: $FRONTEND_DIR"
}

ensure_root_package() {
  [[ -f "$ROOT_DIR/package.json" ]] || fail "package.json da raiz não encontrado: $ROOT_DIR/package.json"
}

PROD_MODE="false"
SKIP_BUILD="false"
MSRPG_FRONTEND_PORT="$DEFAULT_FRONTEND_PORT"

parse_options() {
  while [[ $# -gt 0 ]]; do
    case "$1" in
      --front-port)
        [[ $# -ge 2 ]] || fail "A opção --front-port exige um valor."
        MSRPG_FRONTEND_PORT="$2"
        shift 2
        ;;
      --prod)
        PROD_MODE="true"
        shift
        ;;
      --skip-build)
        SKIP_BUILD="true"
        shift
        ;;
      -h|--help|help)
        print_help
        exit 0
        ;;
      *)
        fail "Opção desconhecida: $1"
        ;;
    esac
  done
}

run_backend_build() {
  ensure_backend_dir
  require_command mvn

  log "Compilando backend..."
  cd "$BACKEND_DIR"
  mvn clean package
}

run_backend_tests() {
  ensure_backend_dir
  require_command mvn

  log "Rodando testes do backend..."
  cd "$BACKEND_DIR"
  mvn test
}

run_backend_jar() {
  ensure_backend_dir
  require_command java
  parse_options "$@"

  [[ -f "$BACKEND_JAR" ]] || fail "JAR não encontrado: $BACKEND_JAR. Rode: ./msrpg.sh backend:build"

  log "Rodando backend via JAR..."
  cd "$BACKEND_DIR"
  java -jar "$BACKEND_JAR"
}

run_backend_maven() {
  ensure_backend_dir
  require_command mvn
  parse_options "$@"

  export MSRPG_DEV_MODE="true"

  log "Rodando backend via Maven..."
  cd "$BACKEND_DIR"
  mvn exec:java -Dexec.mainClass="$MAIN_CLASS"
}

run_frontend() {
  ensure_frontend_dir
  require_command npm
  parse_options "$@"

  log "Rodando frontend Angular (Porta: $MSRPG_FRONTEND_PORT)..."
  cd "$FRONTEND_DIR"

  if npm run | grep -qE "^[[:space:]]+start"; then
    npm start -- --port "$MSRPG_FRONTEND_PORT"
  else
    npx ng serve --port "$MSRPG_FRONTEND_PORT"
  fi
}

run_frontend_build() {
  ensure_frontend_dir
  require_command npm
  parse_options "$@"

  log "Compilando frontend Angular..."
  cd "$FRONTEND_DIR"

  if [[ "$PROD_MODE" == "true" ]]; then
    npm run build -- --configuration production
  else
    npm run build
  fi
}

run_frontend_tests() {
  ensure_frontend_dir
  require_command npm
  parse_options "$@"

  log "Rodando testes do frontend..."
  cd "$FRONTEND_DIR"
  npm test
}

cleanup_processes() {
  if [[ -n "${BACKEND_PID:-}" ]] && kill -0 "$BACKEND_PID" 2>/dev/null; then
    log "Encerrando backend PID=$BACKEND_PID..."
    kill "$BACKEND_PID" 2>/dev/null || true
  fi

  if [[ -n "${FRONTEND_PID:-}" ]] && kill -0 "$FRONTEND_PID" 2>/dev/null; then
    log "Encerrando frontend PID=$FRONTEND_PID..."
    kill "$FRONTEND_PID" 2>/dev/null || true
  fi
}

run_dev() {
  parse_options "$@"

  export MSRPG_DEV_MODE="true"

  ensure_backend_dir
  ensure_frontend_dir
  require_command mvn
  require_command npm

  trap cleanup_processes EXIT INT TERM

  log "Rodando backend + frontend..."

  (
    cd "$BACKEND_DIR"
    mvn exec:java -Dexec.mainClass="$MAIN_CLASS"
  ) &
  BACKEND_PID=$!

  (
    cd "$FRONTEND_DIR"
    if npm run | grep -qE "^[[:space:]]+start"; then
      npm start -- --port "$MSRPG_FRONTEND_PORT"
    else
      npx ng serve --port "$MSRPG_FRONTEND_PORT"
    fi
  ) &
  FRONTEND_PID=$!

  wait "$BACKEND_PID" "$FRONTEND_PID"
}

run_electron_dev() {
  parse_options "$@"

  ensure_root_package
  require_command npm

  cd "$ROOT_DIR"

  if [[ "$SKIP_BUILD" != "true" ]]; then
    log "Compilando backend antes de abrir Electron..."
    run_backend_build
  fi

  log "Rodando Electron em modo dev..."
  if npm run | grep -qE "^[[:space:]]+electron:dev"; then
    npm run electron:dev
  else
    npx electron .
  fi
}

run_build_all() {
  parse_options "$@"
  run_backend_build
  run_frontend_build "$@"
}

run_electron_dist() {
  parse_options "$@"
  ensure_root_package
  require_command npm

  cd "$ROOT_DIR"

  if [[ "$SKIP_BUILD" != "true" ]]; then
    run_build_all "$@"
  fi

  cd "$ROOT_DIR"

  log "Gerando pacote desktop com electron-builder..."
  npx electron-builder
}

run_install() {
  parse_options "$@"
  require_command npm

  log "Instalando dependências npm da raiz..."
  cd "$ROOT_DIR"
  npm install

  if [[ -f "$FRONTEND_DIR/package.json" ]]; then
    log "Instalando dependências npm do frontend..."
    cd "$FRONTEND_DIR"
    npm install
  fi
}

run_clean() {
  parse_options "$@"
  log "Limpando artefatos..."

  rm -rf "$BACKEND_DIR/target"
  rm -rf "$FRONTEND_DIR/dist"
  rm -rf "$FRONTEND_DIR/.angular"
  rm -rf "$ROOT_DIR/release"
  rm -rf "$ROOT_DIR/dist"

  log "Limpeza concluída."
}

main() {
  local command="${1:-help}"
  shift || true

  case "$command" in
    -h|--help|help)
      print_help
      ;;
    backend)
      run_backend_maven "$@"
      ;;
    backend:jar)
      run_backend_jar "$@"
      ;;
    backend:build)
      run_backend_build "$@"
      ;;
    backend:test)
      run_backend_tests "$@"
      ;;
    frontend)
      run_frontend "$@"
      ;;
    frontend:build)
      run_frontend_build "$@"
      ;;
    frontend:test)
      run_frontend_tests "$@"
      ;;
    dev)
      run_dev "$@"
      ;;
    electron)
      run_electron_dev "$@"
      ;;
    electron:dist)
      run_electron_dist "$@"
      ;;
    build)
      run_build_all "$@"
      ;;
    install)
      run_install "$@"
      ;;
    clean)
      run_clean "$@"
      ;;
    *)
      fail "Comando desconhecido: $command\nRode './msrpg.sh help' para ver as opções."
      ;;
  esac
}

main "$@"