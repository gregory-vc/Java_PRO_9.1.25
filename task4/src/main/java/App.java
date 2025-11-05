import config.AppConfig;
import domain.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import service.UserService;

private static final AtomicLong USERNAME_COUNTER = new AtomicLong(System.currentTimeMillis());

private static String withUniqueNumber(String baseUsername) {
    long suffix = USERNAME_COUNTER.getAndIncrement();
    return baseUsername + "_" + suffix;
}

void main() {

    try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class)) {
        UserService us = ctx.getBean(UserService.class);
        User user1 = us.create(new User(0, withUniqueNumber("user1")));
        IO.println(user1);

        User user2 = new User(user1.id(), withUniqueNumber("user2"));
        int affected = us.update(user2);
        IO.println(affected);

        Optional<User> user3 = us.findById(user2.id());
        User user4 = user3.orElse(new User(0, "empty"));
        IO.println(user4);

        if (us.deleteById(user4.id())) {
            IO.println("user " + user4.id() + " successfully delete");
        } else {
            IO.println("user " + user4.id() + " not delete");
        }
    }
}
