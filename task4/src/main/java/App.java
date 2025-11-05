import config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import service.UserService;

void main() {

    try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class)) {
        UserService us = ctx.getBean(UserService.class);
        IO.println("123");
    }
}
