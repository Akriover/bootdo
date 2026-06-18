package com.example.usercrud.controller;

import com.example.usercrud.entity.User;
import com.example.usercrud.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * REST 端点 — /api/users
 *
 * Swagger UI: http://localhost:8080/swagger-ui.html
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户信息录入系统的 CRUD 端点")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // ===== R: 查询 =====

    @GetMapping
    @Operation(summary = "分页查询用户列表",
               description = "支持分页、排序、按字段过滤")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功")
    })
    public Page<User> list(
            @Parameter(description = "页码 (从 0 开始)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "排序方向 (asc/desc)") @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortBy));
        return service.list(pageable);
    }

    @GetMapping("/all")
    @Operation(summary = "全量查询", description = "不分页, 直接返回所有用户")
    public List<User> listAll() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "按 ID 查询")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "找到用户"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public User getById(@Parameter(description = "用户 ID") @PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/by-username/{username}")
    @Operation(summary = "按用户名查询")
    public User getByUsername(@Parameter(description = "用户名") @PathVariable String username) {
        return service.getByUsername(username);
    }

    // ===== C: 新增 =====

    @PostMapping
    @Operation(summary = "新增用户")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "创建成功, 返回 Location 头"),
        @ApiResponse(responseCode = "400", description = "字段校验失败"),
        @ApiResponse(responseCode = "409", description = "username 或 email 已存在")
    })
    public ResponseEntity<User> create(
            @Parameter(description = "新用户数据") @Valid @RequestBody User input,
            UriComponentsBuilder uriBuilder
    ) {
        User saved = service.create(input);
        URI location = uriBuilder.path("/api/users/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    // ===== U: 更新 =====

    @PutMapping("/{id}")
    @Operation(summary = "完整更新用户", description = "所有字段必填, 校验唯一性")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "409", description = "username 或 email 冲突")
    })
    public User update(
            @PathVariable Long id,
            @Valid @RequestBody User input
    ) {
        return service.update(id, input);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "部分更新用户", description = "只更新请求体中非 null 的字段")
    public User patch(
            @PathVariable Long id,
            @RequestBody User patch
    ) {
        return service.patch(id, patch);
    }

    // ===== D: 删除 =====

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "删除成功, 无返回体"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
