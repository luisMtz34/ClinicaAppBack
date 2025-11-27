# Etapa 1: Construcción del JAR
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar el pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Compilar y generar el jar
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiar el jar generado
COPY --from=build /app/target/*.jar app.jar

# Render usa la variable de entorno PORT. Usamos sh -c para expandirla.
CMD ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
