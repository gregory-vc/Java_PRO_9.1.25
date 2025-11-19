package task5.app.web.mapper;

import org.springframework.stereotype.Component;
import task5.app.web.dto.ProductResponse;
import task5.domain.Product;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getAccountNumber(),
                product.getBalance(),
                product.getProductType()
        );
    }
}
