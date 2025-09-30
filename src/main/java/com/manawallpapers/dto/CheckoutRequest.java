package com.manawallpapers.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CheckoutRequest {

    @NotEmpty
    private List<CartItem> items;

    @Data
    public static class CartItem {
        @NotNull
        private UUID wallpaperId;

        @NotNull
        private Integer quantity = 1;
    }
}