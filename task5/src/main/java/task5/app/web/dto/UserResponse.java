package task5.app.web.dto;

import java.util.List;

public record UserResponse(Long id, String username, List<ProductResponse> products) {

}
