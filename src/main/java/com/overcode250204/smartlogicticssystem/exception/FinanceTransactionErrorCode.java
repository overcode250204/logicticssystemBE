package com.overcode250204.smartlogicticssystem.exception;

import com.overcode250204.smartlogicticssystem.base.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum FinanceTransactionErrorCode implements BaseErrorCode {
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.TRANSACTION_NOT_FOUND),
    CODE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, Messages.CODE_ALREADY_EXISTS),
    ROLE_HAS_NO_PERMISSION(HttpStatus.FORBIDDEN, Messages.ROLE_HAS_NO_PERMISSION),
    INVALID_TRANSACTION_TYPE_FOR_ROLE(HttpStatus.BAD_REQUEST, Messages.INVALID_TRANSACTION_TYPE_FOR_ROLE),
    ONLY_PENDING_INCOME_CAN_BE_APPROVED(HttpStatus.BAD_REQUEST, Messages.ONLY_PENDING_INCOME_CAN_BE_APPROVED),
    ONLY_PENDING_INCOME_CAN_BE_REJECTED(HttpStatus.BAD_REQUEST, Messages.ONLY_PENDING_INCOME_CAN_BE_REJECTED),
    APPROVED_TRANSACTION_REQUIRES_ADMIN(HttpStatus.FORBIDDEN, Messages.APPROVED_TRANSACTION_REQUIRES_ADMIN),
    RECEIPT_NOT_FOUND(HttpStatus.NOT_FOUND, Messages.RECEIPT_NOT_FOUND);

    private final HttpStatus httpStatus;
    private final String message;

    FinanceTransactionErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public static class Messages {
        public static final String TRANSACTION_NOT_FOUND = "Finance transaction not found";
        public static final String CODE_ALREADY_EXISTS = "Finance transaction code already exists";
        public static final String ROLE_HAS_NO_PERMISSION = "Role has no permission";
        public static final String INVALID_TRANSACTION_TYPE_FOR_ROLE = "Invalid transaction type for this role";
        public static final String ONLY_PENDING_INCOME_CAN_BE_APPROVED = "Only pending income transactions can be approved";
        public static final String ONLY_PENDING_INCOME_CAN_BE_REJECTED = "Only pending income transactions can be rejected";
        public static final String APPROVED_TRANSACTION_REQUIRES_ADMIN = "Only admin can update approved transactions";
        public static final String RECEIPT_NOT_FOUND = "Transaction receipt was not found";

        public static final String CODE_REQUIRED = "Transaction code is required";
        public static final String CODE_TOO_LONG = "Transaction code must be less than 50 characters";
        public static final String TYPE_REQUIRED = "Transaction type is required";
        public static final String AMOUNT_REQUIRED = "Amount is required";
        public static final String AMOUNT_POSITIVE = "Amount must be greater than 0";
        public static final String TRANSACTION_DATE_REQUIRED = "Transaction date is required";
        public static final String PAYMENT_METHOD_REQUIRED = "Payment method is required";
        public static final String DESCRIPTION_TOO_LONG = "Description must be less than 500 characters";
        public static final String RECEIPT_IMAGE_KEY_TOO_LONG = "Receipt image key must be less than 500 characters";
        public static final String REJECT_REASON_REQUIRED = "Reject reason is required";
        public static final String REJECT_REASON_TOO_LONG = "Reject reason must be less than 500 characters";
    }
}
