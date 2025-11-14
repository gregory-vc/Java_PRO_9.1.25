package task5.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import task5.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
}