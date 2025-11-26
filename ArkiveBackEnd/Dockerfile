# --- GIAI ĐOẠN 1: Build (Dùng để biên dịch code) ---
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# 1. Chỉ copy file pom.xml trước
# Bước này cực kỳ quan trọng: Nếu bạn không sửa file pom.xml, Docker sẽ dùng lại cache của bước này
# và bước tải thư viện bên dưới, giúp build lại chỉ mất vài giây thay vì vài phút.
COPY pom.xml .

# 2. Tải toàn bộ thư viện về chế độ offline
RUN mvn dependency:go-offline

# 3. Bây giờ copy source code
# Vì source code thay đổi thường xuyên, ta để bước này sau cùng để tận dụng cache của các bước trên.
COPY src ./src

# 4. Build gói jar
RUN mvn clean package -DskipTests

# --- GIAI ĐOẠN 2: Run (Dùng để chạy ứng dụng - nhẹ hơn) ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy file jar từ giai đoạn build qua
# Đổi tên thành app.jar
COPY --from=build /app/target/*.jar app.jar

# Spring Boot mặc định chạy cổng 8080
# Dùng EXPOSE 8080 để đúng với thực tế
EXPOSE 8080

# Chạy ứng dụng
CMD ["java", "-jar", "app.jar"]