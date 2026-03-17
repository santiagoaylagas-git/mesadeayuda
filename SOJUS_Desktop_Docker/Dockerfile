# ============================================================
# Dockerfile — SOJUS HelpDesk Judicial · Frontend
# React Native (Expo ~51) - Web Development Server
# Autor: Santiago Aylagas
# Fecha: 17 de Marzo de 2026
# Versión: 2.0.0
# ============================================================

FROM node:18-alpine

LABEL maintainer="Santiago Aylagas"
LABEL description="SOJUS HelpDesk Judicial - Frontend (Expo Web Server)"
LABEL version="2.0.0"
LABEL project="Sistema de Gestión de Mesa de Ayuda - Poder Judicial"

WORKDIR /app

# Copiar archivos de dependencias primero (caché de Docker)
COPY 01_Repositorio_Frontend/package.json 01_Repositorio_Frontend/package-lock.json ./

# Instalar dependencias
RUN npm ci --legacy-peer-deps

# Copiar código fuente del frontend
COPY 01_Repositorio_Frontend/ .

# Copiar plan de pruebas UI como documentación interna
COPY 02_Plan_de_Pruebas_UI/ /docs/Plan_de_Pruebas_UI/

# Variables de entorno
ENV EXPO_PUBLIC_API_URL=http://host.docker.internal:8080
ENV NODE_ENV=development

# Puerto de Expo Web
EXPOSE 19006

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:19006/ || exit 1

# Iniciar Expo en modo web (usa npx, no expo-cli global deprecado)
CMD ["npx", "expo", "start", "--web", "--port", "19006", "--host", "0.0.0.0"]
