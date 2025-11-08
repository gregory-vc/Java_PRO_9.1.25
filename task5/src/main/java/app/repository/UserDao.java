package app.repository;

import domain.User;
import infra.db.DbPool;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Optional;

@Repository
public class UserDao {
    private final DbPool dbPool;

    public UserDao(DbPool dbPool) {
        this.dbPool = dbPool;
    }

    public User create(User user) {
        final String sql = "insert into users(username) values (?);";

        try (Connection c = dbPool.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.username());
            int updated = ps.executeUpdate();
            if (updated != 1) {
                throw new IllegalStateException("Insert affected " + updated + " rows");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new User(rs.getLong(1), user.username());
                }
                throw new IllegalStateException("Driver returned no generated key");
            }
        } catch (SQLException e) {
            throw new RuntimeException("insert failed:", e);
        }
    }

    public Optional<User> findById(long id) {
        final String sql = "select id, username from users where id = ?;";

        try (Connection c = dbPool.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(
                            rs.getLong("id"),
                            rs.getString("username")
                    ));
                }

                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById failed:", e);
        }
    }

    public int update(User user) {
        final String sql = "update users set username = ? where id = ?;";

        try (Connection c = dbPool.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user.username());
            ps.setLong(2, user.id());
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("update failed:", e);
        }
    }

    public boolean deleteById(long id) {
        final String sql = "delete from users where id = ?;";

        try (Connection c = dbPool.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("delete failed:", e);
        }
    }
}
