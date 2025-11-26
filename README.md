## Mô tả ngắn
Repo chứa ứng dụng Java (adjust nếu dùng Gradle/Maven khác). README này mô tả cách build bằng Docker và thiết lập CI/CD (GitHub Actions) để build, test và đẩy image lên registry.

## Yêu cầu
- Docker installed
- Java build tool (Maven/Gradle) tương ứng
- Tài khoản registry (Docker Hub hoặc GitHub Container Registry)
- Secrets trên GitHub: REGISTRY (ví dụ docker.io or ghcr.io), REGISTRY_USERNAME, REGISTRY_TOKEN

## Build & chạy local với Docker
1. Tạo Dockerfile ở root (ví dụ multi-stage build cho Java).
2. Build image:
```bash
docker build -t <registry>/<user>/<repo>:<tag> .
```
3. Chạy container:
```bash
docker run --rm -p 8080:8080 <registry>/<user>/<repo>:<tag>
```

(Thay <tag> = latest hoặc v1.0.0; thay registry/user/repo theo cấu hình.)

## CI/CD (GitHub Actions) — ví dụ workflow
Tạo file .github/workflows/ci-cd.yml:

```yaml
name: CI/CD

on:
    push:
        branches: [ main ]
    pull_request:
        branches: [ main ]

jobs:
    build-test:
        runs-on: ubuntu-latest
        steps:
            - uses: actions/checkout@v4
            - name: Set up JDK
                uses: actions/setup-java@v4
                with:
                    distribution: temurin
                    java-version: '17'
            - name: Build & Test
                run: |
                    # Nếu dùng Maven:
                    mvn -B -DskipTests=false package
                    # Nếu dùng Gradle:
                    # ./gradlew build

    docker-build-push:
        needs: build-test
        runs-on: ubuntu-latest
        steps:
            - uses: actions/checkout@v4
            - name: Set up QEMU
                uses: docker/setup-qemu-action@v2
            - name: Set up Docker Buildx
                uses: docker/setup-buildx-action@v2
            - name: Login to registry
                uses: docker/login-action@v2
                with:
                    registry: ${{ secrets.REGISTRY }}
                    username: ${{ secrets.REGISTRY_USERNAME }}
                    password: ${{ secrets.REGISTRY_TOKEN }}
            - name: Build and push Docker image
                uses: docker/build-push-action@v4
                with:
                    context: .
                    push: true
                    tags: |
                        ${{ secrets.REGISTRY }}/${{ secrets.REGISTRY_USERNAME }}/repo:latest
                        ${{ secrets.REGISTRY }}/${{ secrets.REGISTRY_USERNAME }}/repo:${{ github.sha }}
```

Giải thích:
- Job đầu build & test code; job thứ hai build image và push khi tests pass.
- Sử dụng tags: latest và commit SHA để traceable image.
- Thay `repo` và biến secrets phù hợp.

## Gợi ý triển khai
- Thêm job deploy riêng (k8s, docker-compose, serverless...) dùng image đã push.
- Thiết lập policy branch protection trên main để yêu cầu CI pass.

## Lưu ý
- Điều chỉnh lệnh build (Maven/Gradle) và Dockerfile theo cấu trúc dự án.
- Bảo mật: lưu credential trong GitHub Secrets, không commit credentials vào repo.
- Kiểm thử local trước khi push để giảm thời gian CI.
