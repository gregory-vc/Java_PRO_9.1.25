package service;

import domain.User;
import org.springframework.stereotype.Service;
import repository.UserDao;

import java.util.Optional;

@Service
public class UserService {
    private final UserDao repo;

    public UserService(UserDao repo) {
        this.repo = repo;
    }

    public User create(User user) {
        return repo.create(user);
    }

    public Optional<User> findById(long id) {
        return repo.findById(id);
    }

    public int update(User user) {
        return repo.update(user);
    }

    public boolean deleteById(long id) {
        return repo.deleteById(id);
    }
}
