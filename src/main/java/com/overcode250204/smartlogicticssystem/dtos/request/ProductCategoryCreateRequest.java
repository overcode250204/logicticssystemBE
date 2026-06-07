package com.overcode250204.smartlogicticssystem.dtos.request;

import com.overcode250204.smartlogicticssystem.exception.CategoryErrorCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCategoryCreateRequest {


    @NotBlank(message = CategoryErrorCode.Messages.CATEGORY_NAME_REQUIRED)
    @Size(max = 100, message = CategoryErrorCode.Messages.CATEGORY_NAME_TOO_LONG)
    private String categoryName;

    @Size(max = 500, message = CategoryErrorCode.Messages.DESCRIPTION_TOO_LONG)
    private String description;
}

