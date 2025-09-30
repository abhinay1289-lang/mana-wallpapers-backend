package com.manawallpapers.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CheckoutResponse {
    private String sessionId;
    private String checkoutUrl;
    private UUID orderId;
}