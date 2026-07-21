package com.overcode250204.smartlogicticssystem.dataSeeder;

import com.overcode250204.smartlogicticssystem.entities.ProductCategory;
import com.overcode250204.smartlogicticssystem.repositories.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductCategoryDataSeeder implements DataSeeder {

    private final ProductCategoryRepository productCategoryRepository;

    @Override
    public int getOrder() {
        return 5;
    }

    @Override
    @Transactional
    public void seed() {
        createCategoryIfNotExists(
                "FOOD",
                "Thực phẩm",
                "Các mặt hàng thực phẩm và thực phẩm tươi sống"
        );

        createCategoryIfNotExists(
                "BEVERAGE",
                "Đồ uống",
                "Các loại nước uống, nước giải khát và sản phẩm dạng lỏng"
        );

        createCategoryIfNotExists(
                "HOUSEHOLD",
                "Đồ gia dụng",
                "Các sản phẩm gia dụng và nhu yếu phẩm hằng ngày"
        );

        createCategoryIfNotExists(
                "PERSONAL_CARE",
                "Chăm sóc cá nhân",
                "Các sản phẩm chăm sóc cá nhân và vệ sinh"
        );

        createCategoryIfNotExists(
                "ELECTRONICS",
                "Điện tử",
                "Thiết bị điện tử và các phụ kiện liên quan"
        );

        log.info("Đã hoàn thành khởi tạo dữ liệu danh mục sản phẩm.");
    }

    private void createCategoryIfNotExists(
            String categoryCode,
            String categoryName,
            String description
    ) {
        boolean exists = productCategoryRepository.findByCategoryCode(categoryCode) != null;

        if (exists) {
            log.info("Danh mục sản phẩm đã tồn tại: {}", categoryCode);
            return;
        }

        ProductCategory category = new ProductCategory();
        category.setCategoryCode(categoryCode);
        category.setCategoryName(categoryName);
        category.setDescription(description);

        productCategoryRepository.save(category);

        log.info("Đã tạo danh mục sản phẩm: {}", categoryCode);
    }
}
