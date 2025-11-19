package task5.app.web.dto;

import task5.domain.ProductType;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String accountNumber,
        BigDecimal balance,
        ProductType productType
) {
}
