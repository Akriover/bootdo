# bootdo (← 现在跑 user-crud-demo, vibe coding 实战 #1)

> 原 bootdo 仓是 6 年前的空仓, 6/18 推上来做 Spring Boot vibe coding demo。
> 保留 bootdo 名字因为它本身就是 Spring Boot demo 仓的意思, 巧合。

---

# User CRUD Demo (Spring Boot 3.5 + JPA + H2)

> 简易用户信息录入系统, 单表 CRUD, Java 17

## 启动

```bash
# Windows 主力机
cd user-crud-demo
.\mvnw.cmd spring-boot:run

# Mac / Linux
./mvnw spring-boot:run
```

启动后访问: <http://localhost:8080/api/users>

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

## 测试 curl

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

## H2 Web Console

<http://localhost:8080/h2-console>
- JDBC URL: `jdbc:h2:mem:userdb`
- 用户名: `sa`
- 密码: (空)

## 数据校验

- `username` 2-20 字符, 唯一
- `email` 合法格式, 唯一
- `phone` 可选, ≤ 20 字符
- 重复 username/email → 409 Conflict
- 字段校验失败 → 400 + 详细错误

## 项目结构

```
src/main/java/com/example/usercrud/
├── UserCrudApplication.java      # 启动类(已生成)
├── entity/User.java              # 实体
├── repository/UserRepository.java # JPA 仓库
├── service/UserService.java      # 业务层
├── controller/UserController.java # REST 端点
└── exception/
    ├── NotFoundException.java
    ├── DuplicateException.java
    └── GlobalExceptionHandler.java
```
