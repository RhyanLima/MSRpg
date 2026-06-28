#!/usr/bin/env bash

set -eou pipefail

PROJECT_NAME="MSRpg"

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$ROOT_DIR/backend"
FRONTEND_DIR="$ROOT_DIR/frontend"

MAIN_CLASS="com.rcl.msrpg.App"
BACKEND_JAR="$BACKEND_DIR/target/msrpg-backend.jar"

DEFAULT_BACKEND_PORT="8080"
DEFAULT_FRONTEND_PORT="4200"
DEFAULT_DATABASE_PATH="$ROOT_DIR/dev-data/database.db"

BACKEND_PID=""
FRONTEND_PID=""

print_help() {
  cat << EOF
$PROJECT_NAME - Script de desenvolvimento

Uso:
  ./msrpg.sh <comando> [opções]

Comandos:
  backend              Roda somente o backend Java/Javalin
  backend:jar          Roda somente o backend usando o JAR gerado
  backend:maven        Roda somente o backend via Maven exec:java
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
  --port PORT          Porta do backend. Padrão: $DEFAULT_BACKEND_PORT
  --front-port PORT    Porta do frontend Angular. Padrão: $DEFAULT_FRONTEND_PORT
  --db PATH            Caminho do SQLite dev. Padrão: $DEFAULT_DATABASE_PATH
  --prod               Usa modo produção quando aplicável
  --skip-build         Pula build antes do electron/electron:dist

Exemplos:
  ./msrpg.sh -h
  ./msrpg.sh backend
  ./msrpg.sh backend:maven --port 8081
  ./msrpg.sh frontend
  ./msrpg.sh dev
  ./msrpg.sh electron
  ./msrpg.sh build
  ./msrpg.sh electron:dist
  ./msrpg.sh clean

Variáveis exportadas:
  MSRPG_PORT
  MSRPG_DATABASE_PATH
  MSRPG_DB_PATH
  MSRPG_DEV_MODE

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

load_defaults() {
  export MSRPG_PORT="${MSRPG_PORT:-$DEFAULT_BACKEND_PORT}"
  export MSRPG_FRONTEND_PORT="${MSRPG_FRONTEND_PORT:-$DEFAULT_FRONTEND_PORT}"

  export MSRPG_DATABASE_PATH="${MSRPG_DATABASE_PATH:-$DEFAULT_DATABASE_PATH}"

  # Compatibilidade caso alguma classe antiga use MSRPG_DB_PATH.
  export MSRPG_DB_PATH="${MSRPG_DB_PATH:-$MSRPG_DATABASE_PATH}"

  export MSRPG_DEV_MODE="${MSRPG_DEV_MODE:-true}"

  mkdir -p "$(dirname "$MSRPG_DATABASE_PATH")"
}

PROD_MODE="false"
SKIP_BUILD="false"

parse_options() {
  load_defaults

  while [[ $# -gt 0 ]]; do
    case "$1" in
      --port)
        [[ $# -ge 2 ]] || fail "A opção --port exige um valor."
        export MSRPG_PORT="$2"
        shift 2
        ;;

      --front-port)
        [[ $# -ge 2 ]] || fail "A opção --front-port exige um valor."
        export MSRPG_FRONTEND_PORT="$2"
        shift 2
        ;;

      --db)
        [[ $# -ge 2 ]] || fail "A opção --db exige um valor."
        export MSRPG_DATABASE_PATH="$2"
        export MSRPG_DB_PATH="$2"
        mkdir -p "$(dirname "$MSRPG_DATABASE_PATH")"
        shift 2
        ;;

      --prod)
        PROD_MODE="true"
        export MSRPG_DEV_MODE="false"
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

show_backend_env() {
  log "Backend port: $MSRPG_PORT"
  log "Database: $MSRPG_DATABASE_PATH"
  log "Dev mode: $MSRPG_DEV_MODE"
  log "Main class: $MAIN_CLASS"
}

run_backend_jar() {
  ensure_backend_dir
  require_command java

  parse_options "$@"

  [[ -f "$BACKEND_JAR" ]] || fail "JAR não encontrado: $BACKEND_JAR. Rode: ./msrpg.sh backend:build"

  show_backend_env

  cd "$BACKEND_DIR"
  java -jar "$BACKEND_JAR"
}

run_backend_maven() {
  ensure_backend_dir
  require_command mvn

  parse_options "$@"

  show_backend_env

  cd "$BACKEND_DIR"
  mvn exec:java -Dexec.mainClass="$MAIN_CLASS"
}

run_backend() {
  parse_options "$@"

  if [[ -f "$BACKEND_JAR" ]]; then
    run_backend_jar "$@"
  else
    log "JAR não encontrado. Usando Maven exec:java..."
    run_backend_maven "$@"
  fi
}

run_frontend() {
  ensure_frontend_dir
  require_command npm

  parse_options "$@"

  log "Rodando frontend Angular..."
  log "Frontend port: $MSRPG_FRONTEND_PORT"

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

  ensure_backend_dir
  ensure_frontend_dir
  require_command mvn
  require_command npm

  trap cleanup_processes EXIT INT TERM

  log "Rodando backend + frontend..."
  log "Backend: http://localhost:$MSRPG_PORT"
  log "Frontend: http://localhost:$MSRPG_FRONTEND_PORT"

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
      run_backend "$@"
      ;;

    backend:jar)
      run_backend_jar "$@"
      ;;

    backend:maven)
      run_backend_maven "$@"
      ;;

    backend:build)
      parse_options "$@"
      run_backend_build
      ;;

    backend:test)
      parse_options "$@"
      run_backend_tests
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
      echo "Comando desconhecido: $command"
      echo
      print_help
      exit 1
      ;;
  esac
}

main "$@"