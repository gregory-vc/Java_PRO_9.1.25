package config;

import infra.db.DbPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:db.properties")
public class AppConfig {

    @Value("${datasource.url}")
    private String url;

    @Value("${datasource.username}")
    private String username;

    @Value("${datasource.password}")
    private String password;

    @Bean(destroyMethod = "close")
    @SuppressWarnings("unused")
    public DbPool dbPool() {
        return DbPool.of(this.url, this.username, this.password);
    }
}
