package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CategoryErrorCode implements BaseErrorCode {
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.CATEGORY_NOT_FOUND),
    CATEGORY_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, Messages.CATEGORY_ALREADY_EXISTS);

    private final String message;
    private final HttpStatus httpStatus;

    CategoryErrorCode(HttpStatus status, String message) {
        this.message = message;
        this.httpStatus = status;
    }

    public static class Messages {
        public static final String CATEGORY_NOT_FOUND = "Category not found";
        public static final String CATEGORY_ALREADY_EXISTS = "Category code already exists";
        public static final String CATEGORY_CODE_REQUIRED = "Category code is required";
        public static final String CATEGORY_CODE_TOO_LONG = "Category code must be less than 50 characters";
        public static final String CATEGORY_NAME_REQUIRED = "Category name is required";
        public static final String CATEGORY_NAME_TOO_LONG = "Category name must be less than 100 characters";
        public static final String DESCRIPTION_TOO_LONG = "Description must be less than 500 characters";
    }
}

