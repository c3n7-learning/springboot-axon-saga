package tech.c3n7.estore.ProductsService.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductsController {
    @Autowired
    private Environment environment;
    @PostMapping
    public String createProduct(@RequestBody CreateProductRestModel createProductRestModel) {
        return "HTTP POST Handled!:: " + createProductRestModel.getTitle();
    }

    @GetMapping
    public String getProduct() {
        return "HTTP GET Handled:: " +environment.getProperty("local.server.port");
    }

    @PutMapping
    public String updateProduct() {
        return "HTTP PUT Handled";
    }

    @DeleteMapping
    public String deleteProduct() {
        return "HTTP DELETE Handled";
    }
}
