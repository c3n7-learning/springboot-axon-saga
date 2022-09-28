package tech.c3n7.estore.ProductsService.command.rest;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class CreateProductRestModel {

    // https://hibernate.org/validator/
    // http://hibernate.org/validator/documentation/
//    @NotBlank(message="Product title is a required field")
    private String title;

    @Min(value=1, message="The minimum price is 1")
    private BigDecimal price;

    @Min(value=1, message="Quantity cannot be lower than 1")
    @Max(value=5, message="Quantity cannot be higher than 5")
    private Integer quantity;
}
