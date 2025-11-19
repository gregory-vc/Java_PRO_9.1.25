package task5.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import task5.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
