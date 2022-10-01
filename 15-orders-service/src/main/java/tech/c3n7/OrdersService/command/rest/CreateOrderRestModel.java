package tech.c3n7.OrdersService.command.rest;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class CreateOrderRestModel {
    @NotBlank(message = "productId is required")
    private String productId;

    @Min(value=1, message="Minimum quantity is 1")
    private int quantity;

    @NotBlank(message = "addressId is required")
    private String addressId;
}
