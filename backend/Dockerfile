# ============================================================
# SOJUS HelpDesk — Dockerfile (Multi-stage Build)
# ============================================================

# --- Stage 1: Build ---
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar pom.xml primero para cachear dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests -B

# --- Stage 2: Runtime ---
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Crear usuario no-root por seguridad
RUN addgroup -S sojus && adduser -S sojus -G sojus

# Copiar el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Cambiar al usuario no-root
USER sojus

# Exponer puerto
EXPOSE 8080

# Health check (usa Actuator para verificar salud del servicio)
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Ejecutar con perfil Docker
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
