package task5.app.web;

import task5.domain.Product;
import task5.domain.ProductType;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String accountNumber,
        BigDecimal balance,
        ProductType productType
) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getAccountNumber(),
                product.getBalance(),
                product.getProductType()
        );
    }
}
