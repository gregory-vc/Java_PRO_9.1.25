package task5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import task5.app.service.UserService;
import task5.domain.User;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class DefaultCommandLineRunner implements CommandLineRunner {

    private static final AtomicLong USERNAME_COUNTER = new AtomicLong(System.currentTimeMillis());
    private static final Logger log = LoggerFactory.getLogger(DefaultCommandLineRunner.class);
    private final UserService userService;

    public DefaultCommandLineRunner(UserService userService) {
        this.userService = userService;
    }

    private static String withUniqueNumber(String baseUsername) {
        long suffix = USERNAME_COUNTER.getAndIncrement();
        return baseUsername + "_" + suffix;
    }

    @Override
    public void run(String... args) {

        log.info("Default CommandLineRunner executed; userService bean: {}", userService.getClass().getSimpleName());

        User user1 = userService.create(new User(withUniqueNumber("user1")));
        log.info("{}, {}", user1.getId(), user1.getUsername());

        user1.setUsername(withUniqueNumber("user2"));

        User user2affected = userService.update(user1);
        log.info("{}, {}", user2affected.getId(), user2affected.getUsername());

        Optional<User> user3 = userService.findById(user2affected.getId());
        User user4 = user3.orElse(new User("empty"));
        log.info("{}, {}", user4.getId(), user4.getUsername());

        userService.deleteById(user4.getId());
        log.info("user {}, {} successfully delete", user4.getId(), user4.getUsername());
    }
}
