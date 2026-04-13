# 构建阶段
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY src /app/src
COPY settings.xml pom.xml /app/

RUN mvn -s /app/settings.xml -f /app/pom.xml clean package -DskipTests

# 运行阶段（修复证书 + 兼容微信云托管）
FROM eclipse-temurin:21-jre

# 安装证书工具 + 更新系统证书 + 强制JVM信任系统证书
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        tzdata \
        curl \
        openssl \
        ca-certificates \
        ca-certificates-java && \
    # 关键：更新系统证书
    update-ca-certificates && \
    # 关键：强制 JVM 使用系统证书（解决 PKIX 问题）
    /var/lib/dpkg/info/ca-certificates-java.postinst configure && \
    # 时区
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /app/target/lebo.jar /app/lebo.jar

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:80/actuator/health || exit 1

# 启动命令增加 JVM 证书参数（双重保险）
CMD ["java", \
     "-Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts", \
     "-Djavax.net.ssl.trustStorePassword=changeit", \
     "-jar", "/app/lebo.jar", \
     "--server.port=80"]