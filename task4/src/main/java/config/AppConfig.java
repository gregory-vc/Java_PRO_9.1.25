package config;

import infra.db.DbPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:db.properties")
@ComponentScan(basePackages = {"service", "repository"})
public class AppConfig {

    @Bean(destroyMethod = "close")
    @SuppressWarnings("unused")
    public DbPool dbPool(Environment env) {
        String url = env.getRequiredProperty("datasource.url");
        String username = env.getRequiredProperty("datasource.username");
        String password = env.getRequiredProperty("datasource.password");

        return DbPool.of(url, username, password);
    }
}
