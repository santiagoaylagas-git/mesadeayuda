# ============================================================
# Dockerfile — SOJUS HelpDesk Judicial · Frontend
# React Native (Expo ~51) - Development / Web Server
# Autor: Santiago Aylagas
# Fecha: 17 de Marzo de 2026
# Versión: 2.0.0
# ============================================================

# ---- Etapa 1: Instalación de dependencias ----
FROM node:18-alpine AS deps

WORKDIR /app

# Instalar Expo CLI globalmente
RUN npm install -g expo-cli@latest

# Copiar archivos de dependencias primero (para aprovechar caché de Docker)
COPY 01_Repositorio_Frontend/package.json 01_Repositorio_Frontend/package-lock.json ./

# Instalar dependencias
RUN npm ci --legacy-peer-deps

# ---- Etapa 2: Aplicación completa ----
FROM node:18-alpine AS app

LABEL maintainer="Santiago Aylagas"
LABEL description="SOJUS HelpDesk Judicial - Frontend (Expo Web Server)"
LABEL version="2.0.0"
LABEL project="Sistema de Gestión de Mesa de Ayuda - Poder Judicial"

WORKDIR /app

# Instalar Expo CLI
RUN npm install -g expo-cli@latest

# Copiar dependencias instaladas desde la etapa anterior
COPY --from=deps /app/node_modules ./node_modules

# Copiar código fuente del frontend
COPY 01_Repositorio_Frontend/ .

# Copiar plan de pruebas UI como documentación
COPY 02_Plan_de_Pruebas_UI/ /docs/Plan_de_Pruebas_UI/

# Variables de entorno
ENV EXPO_PUBLIC_API_URL=http://host.docker.internal:8080
ENV NODE_ENV=development

# Puerto de Expo Web
EXPOSE 19006

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:19006/ || exit 1

# Iniciar Expo en modo web
CMD ["npx", "expo", "start", "--web", "--port", "19006", "--host", "0.0.0.0"]
