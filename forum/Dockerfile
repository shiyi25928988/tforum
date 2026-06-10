# ========================================
# 后端 Dockerfile - Spring Boot
# ========================================
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# 复制 jar
COPY target/forum-*.jar app.jar

# 暴露端口
EXPOSE 8081

# 启动
ENTRYPOINT ["java", "-jar", "app.jar"]
