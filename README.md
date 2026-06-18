# User CRUD Demo (Spring Boot 3.5 + JPA)

> 简易用户信息录入系统, 单表 CRUD, Java 17
> **Vibe coding 实战 #1** (2026-06-18, AI 出活 ~93%)

## 启动

### 1. 默认 — H2 内存库 (零依赖, 启动即用)
```bash
cd user-crud-demo
./mvnw spring-boot:run        # Mac / Linux
.\mvnw.cmd spring-boot:run    # Windows
```
启动后:
- API: <http://localhost:8080/api/users>
- **Swagger UI: <http://localhost:8080/swagger-ui.html>** ← 新加!
- H2 Console: <http://localhost:8080/h2-console> (JDBC URL: `jdbc:h2:mem:userdb`, user: `sa`, 密码空)

### 2. 切 MySQL (需要先有 MySQL 8.x 实例)
```bash
# 1) 创建数据库
mysql -u root -p
> CREATE DATABASE userdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
> EXIT;

# 2) 启动, 指定 prod profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# 或者
java -jar target/user-crud-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

**MySQL JDBC URL** (`application-prod.properties`):
```
jdbc:mysql://localhost:3306/userdb?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8mb4&allowPublicKeyRetrieval=true
```
默认 user=`root` password=`root`, **生产请改!**

## API 速查

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/users | 新增 |
| GET  | /api/users | 分页列表 (page/size/sortBy/direction) |
| GET  | /api/users/all | 全量列表 |
| GET  | /api/users/{id} | 按 id 查 |
| GET  | /api/users/by-username/{username} | 按 username 查 |
| PUT  | /api/users/{id} | 完整更新 |
| PATCH | /api/users/{id} | 部分更新 |
| DELETE | /api/users/{id} | 删除 |

**Swagger UI 浏览器调试**: <http://localhost:8080/swagger-ui.html> (推荐用这个, 不用手写 curl)

## 测试 curl (不用 Swagger 也能用)

```bash
# 新增
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@example.com","phone":"13800000001"}'

# 列表
curl http://localhost:8080/api/users

# 按 id 查
curl http://localhost:8080/api/users/1

# 改邮箱
curl -X PATCH http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"email":"new@example.com"}'

# 删
curl -X DELETE http://localhost:8080/api/users/1
```

## 数据校验

- `username` 2-20 字符, 唯一
- `email` 合法格式, 唯一
- `phone` 可选, ≤ 20 字符
- 重复 username/email → 409 Conflict
- 字段校验失败 → 400 + 详细错误

## Profile 切换 (dev vs prod)

| Profile | 库 | 控制台 | 用途 |
|---------|-----|--------|------|
| `dev` (默认) | H2 内存 | H2 Web + Swagger UI | 本地开发, 零依赖 |
| `prod` | MySQL 8.x | Swagger UI | 生产, 需要 MySQL 实例 |

切法: `application.properties` 改 `spring.profiles.active=prod`, 或命令行 `--spring.profiles.active=prod`

**生产安全**:
- `ddl-auto=validate` (不自动改表, 严格模式)
- utf8mb4 (支持 emoji + 中文)
- 时区 Asia/Shanghai (避免时间字段差 8 小时)

## 项目结构

```
src/main/java/com/example/usercrud/
├── UserCrudApplication.java      # 启动类
├── config/
│   └── OpenApiConfig.java        # Swagger 元数据 ← 新加
├── entity/User.java              # 实体 (+ @Schema 注解)
├── repository/UserRepository.java # JPA 仓库
├── service/UserService.java      # 业务层
├── controller/UserController.java # REST 端点 (+ @Tag/@Operation 注解)
└── exception/
    ├── NotFoundException.java
    ├── DuplicateException.java
    └── GlobalExceptionHandler.java
```

## CI (GitHub Actions)

`.github/workflows/ci.yml` — push 时自动跑 `mvn verify`
- Linux + JDK 17 + Maven
- H2 dev profile 跑测试
- 不测 MySQL (需要 docker service, 留待升级)

## 版本栈

- Spring Boot 3.5.0
- Java 17 (LTS)
- JPA + Hibernate 6.x
- springdoc-openapi 2.8.6
- H2 2.x (dev) / MySQL 8.x (prod)
