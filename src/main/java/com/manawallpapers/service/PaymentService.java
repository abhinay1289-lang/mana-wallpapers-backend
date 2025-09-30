package com.manawallpapers.service;

import com.manawallpapers.dto.CheckoutRequest;
import com.manawallpapers.dto.CheckoutResponse;
import com.manawallpapers.entity.*;
import com.manawallpapers.exception.BadRequestException;
import com.manawallpapers.repository.OrderRepository;
import com.manawallpapers.repository.WallpaperRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentService {

    private final OrderRepository orderRepository;
    private final WallpaperRepository wallpaperRepository;
    private final DownloadService downloadService;
    private final EmailService emailService;
    private final RazorpayClient razorpayClient;

    public CheckoutResponse createOrder(CheckoutRequest request, User buyer) {
        try {
            // Create order in our database
            com.manawallpapers.entity.Order localOrder = createLocalOrder(request, buyer);

            // Create Razorpay order
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", localOrder.getTotal().multiply(new BigDecimal(100)).intValue()); // Amount in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", localOrder.getId().toString());

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            // Update local order with Razorpay order ID
            localOrder.setProviderPaymentId(razorpayOrder.get("id"));
            orderRepository.save(localOrder);

            return CheckoutResponse.builder()
                    .orderId(localOrder.getId())
                    .razorpayOrderId(razorpayOrder.get("id"))
                    .build();
        } catch (RazorpayException e) {
            log.error("Error creating Razorpay order", e);
            throw new BadRequestException("Failed to create payment order");
        }
    }

    public void verifyPayment(String orderId, String paymentId, String signature) {
        try {
            com.manawallpapers.entity.Order order = orderRepository.findById(java.util.UUID.fromString(orderId))
                    .orElseThrow(() -> new BadRequestException("Order not found"));

            String secret = razorpayClient.getApiSecret();
            String payload = order.getProviderPaymentId() + "|" + paymentId;

            if (Utils.verifyPaymentSignature(new JSONObject().put("razorpay_payment_id", paymentId).put("razorpay_order_id", order.getProviderPaymentId()).put("razorpay_signature", signature), secret)) {
                // Payment successful
                order.setStatus(com.manawallpapers.entity.Order.OrderStatus.PAID);
                orderRepository.save(order);

                // Grant access to wallpapers
                downloadService.createDownloadTokens(order);

                // Send confirmation email
                emailService.sendOrderConfirmation(order);

                log.info("Successfully processed payment for order: {}", orderId);
            } else {
                // Payment failed
                order.setStatus(com.manawallpapers.entity.Order.OrderStatus.FAILED);
                orderRepository.save(order);
                throw new BadRequestException("Payment verification failed");
            }
        } catch (RazorpayException e) {
            log.error("Error verifying payment", e);
            throw new BadRequestException("Payment verification failed");
        }
    }

    private com.manawallpapers.entity.Order createLocalOrder(CheckoutRequest request, User buyer) {
        com.manawallpapers.entity.Order order = new com.manawallpapers.entity.Order();
        order.setBuyer(buyer);
        order.setCurrency("INR");
        order.setProvider("razorpay");
        order.setStatus(com.manawallpapers.entity.Order.OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CheckoutRequest.CartItem cartItem : request.getItems()) {
            Wallpaper wallpaper = wallpaperRepository.findById(cartItem.getWallpaperId())
                    .orElseThrow(() -> new BadRequestException("Wallpaper not found: " + cartItem.getWallpaperId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setWallpaper(wallpaper);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceCents(wallpaper.getPriceCents());

            orderItems.add(orderItem);
            total = total.add(wallpaper.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setOrderItems(orderItems);
        order.setTotal(total);

        return orderRepository.save(order);
    }
}
