package com.example.usercrud.service;

import com.example.usercrud.entity.User;
import com.example.usercrud.exception.NotFoundException;
import com.example.usercrud.exception.DuplicateException;
import com.example.usercrud.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户业务层 — 事务边界 + 业务校验
 *
 * 错误用业务异常往上抛, Controller 层统一翻译成 HTTP 状态码
 */
@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    /** 分页查询 */
    public Page<User> list(Pageable pageable) {
        return repo.findAll(pageable);
    }

    /** 简单列表(不分页) — 内部用 */
    public List<User> listAll() {
        return repo.findAll();
    }

    public User get(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: id=" + id));
    }

    public User getByUsername(String username) {
        return repo.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: username=" + username));
    }

    /** 新增 — 校验 username/email 唯一 */
    @Transactional
    public User create(User input) {
        if (repo.existsByUsername(input.getUsername())) {
            throw new DuplicateException("username already exists: " + input.getUsername());
        }
        if (repo.existsByEmail(input.getEmail())) {
            throw new DuplicateException("email already exists: " + input.getEmail());
        }
        // id 强制 null, 避免客户端塞值
        input.setId(null);
        return repo.save(input);
    }

    /** 完整更新 */
    @Transactional
    public User update(Long id, User input) {
        User existing = get(id);
        // 如果改了 username/email, 校验唯一
        if (!existing.getUsername().equals(input.getUsername())
                && repo.existsByUsername(input.getUsername())) {
            throw new DuplicateException("username already exists: " + input.getUsername());
        }
        if (!existing.getEmail().equals(input.getEmail())
                && repo.existsByEmail(input.getEmail())) {
            throw new DuplicateException("email already exists: " + input.getEmail());
        }
        existing.setUsername(input.getUsername());
        existing.setEmail(input.getEmail());
        existing.setPhone(input.getPhone());
        return repo.save(existing);
    }

    /** 部分更新 — 只更新非 null 字段 */
    @Transactional
    public User patch(Long id, User patch) {
        User existing = get(id);
        if (patch.getUsername() != null && !patch.getUsername().equals(existing.getUsername())) {
            if (repo.existsByUsername(patch.getUsername())) {
                throw new DuplicateException("username already exists: " + patch.getUsername());
            }
            existing.setUsername(patch.getUsername());
        }
        if (patch.getEmail() != null && !patch.getEmail().equals(existing.getEmail())) {
            if (repo.existsByEmail(patch.getEmail())) {
                throw new DuplicateException("email already exists: " + patch.getEmail());
            }
            existing.setEmail(patch.getEmail());
        }
        if (patch.getPhone() != null) {
            existing.setPhone(patch.getPhone());
        }
        return repo.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("User not found: id=" + id);
        }
        repo.deleteById(id);
    }
}
