FROM maven:3.9.6-eclipse-temurin-21 as builder

WORKDIR /app

COPY src /app/src
COPY settings.xml pom.xml /app/

RUN mvn -s /app/settings.xml -f /app/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre

RUN apt-get update && \
    apt-get install -y --no-install-recommends tzdata curl ca-certificates && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /app/target/lebo.jar /app/lebo.jar

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:80/actuator/health || exit 1

CMD ["java", "-jar", "/app/lebo.jar", "--server.port=80"]