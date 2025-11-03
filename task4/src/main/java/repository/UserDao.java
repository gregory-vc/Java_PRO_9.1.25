package repository;

import domain.User;
import infra.db.DbPool;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserDao {
    private final DbPool dbPool;

    public UserDao(DbPool dbPool) {
        this.dbPool = dbPool;
    }

    public User create(User user) {
        return new User(1, "12");
    }

    public Optional<User> findById(long id) {
        return Optional.of(new User(1, "12"));
    }

    public User update(User user) {
        return new User(1, "12");
    }

    public boolean deleteById(long id) {
        return false;
    }
}
