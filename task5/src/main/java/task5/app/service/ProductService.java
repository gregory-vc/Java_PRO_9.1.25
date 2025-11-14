package task5.app.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task5.app.repository.ProductRepository;
import task5.domain.Product;

import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return repo.findById(id);
    }
}
