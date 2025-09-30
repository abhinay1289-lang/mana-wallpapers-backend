package com.manawallpapers.service;

import com.manawallpapers.dto.CheckoutRequest;
import com.manawallpapers.dto.CheckoutResponse;
import com.manawallpapers.entity.*;
import com.manawallpapers.exception.BadRequestException;
import com.manawallpapers.repository.OrderRepository;
import com.manawallpapers.repository.WallpaperRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentService {

    private final OrderRepository orderRepository;
    private final WallpaperRepository wallpaperRepository;
    private final DownloadService downloadService;
    private final EmailService emailService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public CheckoutResponse createCheckoutSession(CheckoutRequest request, User buyer) {
        try {
            // Create order
            Order order = createOrder(request, buyer);

            // Create Stripe session
            SessionCreateParams.Builder sessionBuilder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(frontendUrl + "/checkout/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(frontendUrl + "/checkout/cancel")
                    .setCustomerEmail(buyer.getEmail())
                    .setPaymentIntentData(
                            SessionCreateParams.PaymentIntentData.builder()
                                    .putMetadata("order_id", order.getId().toString())
                                    .build()
                    );
            // Add line items
            for (OrderItem item : order.getOrderItems()) {
                SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                        .setQuantity((long) item.getQuantity())
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(order.getCurrency().toLowerCase())
                                .setUnitAmount((long) item.getPriceCents())
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(item.getWallpaper().getTitle())
                                        .setDescription("Digital wallpaper download")
                                        .addImage(item.getWallpaper().getThumbnailKey())
                                        .build())
                                .build())
                        .build();

                sessionBuilder.addLineItem(lineItem);
            }

            Session session = Session.create(sessionBuilder.build());

            // Update order with session ID
            order.setProviderPaymentId(session.getId());
            orderRepository.save(order);

            return CheckoutResponse.builder()
                    .sessionId(session.getId())
                    .checkoutUrl(session.getUrl())
                    .orderId(order.getId())
                    .build();

        } catch (StripeException e) {
            log.error("Error creating Stripe session", e);
            throw new BadRequestException("Failed to create payment session");
        }
    }

    public void handleWebhook(Event event) {
        log.info("Handling webhook event: {}", event.getType());

        switch (event.getType()) {
            case "checkout.session.completed":
                handleCheckoutSessionCompleted(event);
                break;
            case "payment_intent.payment_failed":
                handlePaymentFailed(event);
                break;
            default:
                log.warn("Unhandled event type: {}", event.getType());
        }
    }

    private void handleCheckoutSessionCompleted(Event event) {
        try {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session == null) {
                log.error("Session is null in webhook event");
                return;
            }

            String orderIdStr = session.getMetadata().get("order_id");
            if (orderIdStr == null) {
                log.error("Order ID not found in session metadata");
                return;
            }

            UUID orderId = UUID.fromString(orderIdStr);
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                log.error("Order not found: {}", orderId);
                return;
            }

            // Update order status
            order.setStatus(Order.OrderStatus.PAID);
            orderRepository.save(order);

            // Generate download tokens
            downloadService.createDownloadTokens(order);

            // Send confirmation email
            emailService.sendOrderConfirmation(order);

            log.info("Successfully processed payment for order: {}", orderId);

        } catch (Exception e) {
            log.error("Error handling checkout session completed webhook", e);
        }
    }

    private void handlePaymentFailed(Event event) {
        // Handle payment failure
        log.info("Payment failed webhook received");
    }

    private Order createOrder(CheckoutRequest request, User buyer) {
        Order order = new Order();
        order.setBuyer(buyer);
        order.setCurrency("USD");
        order.setProvider("stripe");
        order.setStatus(Order.OrderStatus.PENDING);

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