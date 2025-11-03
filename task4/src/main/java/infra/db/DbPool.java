package infra.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class DbPool implements AutoCloseable {
    private final HikariDataSource ds;

    private DbPool(HikariDataSource ds) {
        this.ds = ds;
    }

    public static DbPool of(String url, String user, String pass) {
        HikariConfig cfg = new HikariConfig();

        cfg.setJdbcUrl(url);
        cfg.setUsername(user);
        cfg.setPassword(pass);

        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(2);
        cfg.setConnectionTimeout(30_000);
        cfg.setIdleTimeout(600_000);
        cfg.setMaxLifetime(1_800_000);
        cfg.setPoolName("app-pool");

        cfg.addDataSourceProperty("ApplicationName", "task4");
        cfg.addDataSourceProperty("reWriteBatchedInserts", "true");

        return new DbPool(new HikariDataSource(cfg));
    }

    public Connection get() throws SQLException {
        return ds.getConnection();
    }

    @Override
    public void close() {
        this.ds.close();
    }
}
