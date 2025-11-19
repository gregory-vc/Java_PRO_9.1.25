package task5.app.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import task5.app.exception.UserNotFoundException;
import task5.app.service.UserService;
import task5.app.web.dto.UserResponse;
import task5.app.web.mapper.UserMapper;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable("id") long id) {
        return userService.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(
                        "User %d not found".formatted(id)
                ));
    }
}
