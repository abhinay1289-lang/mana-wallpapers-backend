package com.manawallpapers.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
public class CheckoutResponse {
    private UUID orderId;
    private String razorpayOrderId;
}
