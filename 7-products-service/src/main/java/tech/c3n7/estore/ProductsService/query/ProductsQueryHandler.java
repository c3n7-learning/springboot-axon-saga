package tech.c3n7.estore.ProductsService.query;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import tech.c3n7.estore.ProductsService.core.data.ProductEntity;
import tech.c3n7.estore.ProductsService.core.data.ProductsRepository;
import tech.c3n7.estore.ProductsService.query.rest.FindProductsQuery;
import tech.c3n7.estore.ProductsService.query.rest.ProductRestModel;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductsQueryHandler {
    private final ProductsRepository productsRepository;

    public ProductsQueryHandler(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    @QueryHandler
    public List<ProductRestModel> findProducts(FindProductsQuery findProductsQuery) {
        List<ProductRestModel> productRestModels = new ArrayList<>();

        List<ProductEntity> storedProducts = productsRepository.findAll();
        for(ProductEntity productEntity: storedProducts) {
            ProductRestModel productRestModel = new ProductRestModel();
            BeanUtils.copyProperties(productEntity, productRestModel);
            productRestModels.add(productRestModel);
        }

        return productRestModels;
    }
}
