# ============================================================
# Dockerfile — SOJUS HelpDesk Judicial · Frontend
# React Native (Expo ~51) - Development Server
# ============================================================

FROM node:18-alpine

LABEL maintainer="Santiago Aylagas"
LABEL description="SOJUS HelpDesk Judicial - Frontend (Expo Dev Server)"
LABEL version="2.0.0"

# Directorio de trabajo
WORKDIR /app

# Instalar Expo CLI globalmente
RUN npm install -g expo-cli@latest

# Copiar archivos de dependencias
COPY package.json package-lock.json ./

# Instalar dependencias
RUN npm ci --legacy-peer-deps

# Copiar código fuente
COPY . .

# Variables de entorno
ENV EXPO_PUBLIC_API_URL=http://host.docker.internal:8080
ENV NODE_ENV=development

# Puerto de Expo Web
EXPOSE 19006

# Iniciar Expo en modo web
CMD ["npx", "expo", "start", "--web", "--port", "19006", "--host", "0.0.0.0"]
