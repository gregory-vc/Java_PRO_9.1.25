package task5.app.web;

import task5.domain.User;

import java.util.List;

public record UserResponse(Long id, String username, List<ProductResponse> products) {

    public static UserResponse from(User user) {
        var products = user.getProducts()
                .stream()
                .map(ProductResponse::from)
                .toList();

        return new UserResponse(user.getId(), user.getUsername(), products);
    }
}
