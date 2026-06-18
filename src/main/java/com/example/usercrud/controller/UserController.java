package com.example.usercrud.controller;

import com.example.usercrud.entity.User;
import com.example.usercrud.service.UserService;
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
 * 标准 CRUD:
 *   POST   /api/users              新增
 *   GET    /api/users              分页列表
 *   GET    /api/users/all          全量列表(不分页)
 *   GET    /api/users/{id}         按 id 查
 *   GET    /api/users/by-username/{username}  按 username 查
 *   PUT    /api/users/{id}         完整更新
 *   PATCH  /api/users/{id}         部分更新
 *   DELETE /api/users/{id}         删除
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // ===== R: 查询 =====

    @GetMapping
    public Page<User> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortBy));
        return service.list(pageable);
    }

    @GetMapping("/all")
    public List<User> listAll() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/by-username/{username}")
    public User getByUsername(@PathVariable String username) {
        return service.getByUsername(username);
    }

    // ===== C: 新增 =====

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User input, UriComponentsBuilder uriBuilder) {
        User saved = service.create(input);
        URI location = uriBuilder.path("/api/users/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    // ===== U: 更新 =====

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @Valid @RequestBody User input) {
        return service.update(id, input);
    }

    @PatchMapping("/{id}")
    public User patch(@PathVariable Long id, @RequestBody User patch) {
        return service.patch(id, patch);
    }

    // ===== D: 删除 =====

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
