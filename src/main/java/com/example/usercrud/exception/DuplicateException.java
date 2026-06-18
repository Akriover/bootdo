package com.example.usercrud.exception;

/** 唯一约束冲突 → 409 */
public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        super(message);
    }
}
