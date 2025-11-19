package task5.app.web.mapper;

import org.springframework.stereotype.Component;
import task5.app.web.dto.UserResponse;
import task5.domain.User;

@Component
public class UserMapper {

    private final ProductMapper productMapper;

    public UserMapper(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public UserResponse toResponse(User user) {
        var products = user.getProducts()
                .stream()
                .map(productMapper::toResponse)
                .toList();

        return new UserResponse(
                user.getId(), user.getUsername(), products
        );
    }
}
