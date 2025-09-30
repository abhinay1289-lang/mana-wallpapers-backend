package com.manawallpapers.controller;

import com.manawallpapers.dto.ApiResponse;
import com.manawallpapers.dto.CheckoutRequest;
import com.manawallpapers.dto.CheckoutResponse;
import com.manawallpapers.entity.User;
import com.manawallpapers.security.CustomUserDetails;
import com.manawallpapers.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<CheckoutResponse>> createOrder(
            @Valid @RequestBody CheckoutRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User buyer = new User();
        buyer.setId(userDetails.getId());
        buyer.setEmail(userDetails.getEmail());
        buyer.setFullName(userDetails.getFullName());

        CheckoutResponse response = paymentService.createOrder(request, buyer);
        return ResponseEntity.ok(ApiResponse.success("Order created", response));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyPayment(
            @RequestParam("order_id") String orderId,
            @RequestParam("payment_id") String paymentId,
            @RequestParam("signature") String signature) {

        paymentService.verifyPayment(orderId, paymentId, signature);
        return ResponseEntity.ok(ApiResponse.success("Payment verified", null));
    }
}
