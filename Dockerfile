# Etapa 1: Construcción del JAR
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar el pom.xml y descargar dependencias primero (cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Compilar y generar el jar
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final ligera
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiar el jar generado
COPY --from=build /app/target/*.jar app.jar

# Ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]
