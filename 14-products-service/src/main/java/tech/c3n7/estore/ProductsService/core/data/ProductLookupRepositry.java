package tech.c3n7.estore.ProductsService.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLookupRepositry extends JpaRepository<ProductLookupEntity, String> {
    ProductLookupEntity findByProductIdOrTitle(String productId, String title);
}
