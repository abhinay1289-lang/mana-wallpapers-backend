package com.manawallpapers.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CheckoutResponse {
    private UUID orderId;
    private String razorpayOrderId;
}
