package com.overcode250204.smartlogicticssystem.dtos.request;

import com.overcode250204.smartlogicticssystem.exception.FinanceTransactionErrorCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinanceTransactionRejectRequest {
    @NotBlank(message = FinanceTransactionErrorCode.Messages.REJECT_REASON_REQUIRED)
    @Size(max = 500, message = FinanceTransactionErrorCode.Messages.REJECT_REASON_TOO_LONG)
    private String rejectReason;
}
