package com.manawallpapers.service;

import com.manawallpapers.entity.Download;
import com.manawallpapers.entity.Order;
import com.manawallpapers.entity.OrderItem;
import com.manawallpapers.repository.DownloadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final DownloadRepository downloadRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void sendOrderConfirmation(Order order) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(order.getBuyer().getEmail());
            message.setSubject("Order Confirmation - Mana Wallpapers #" + order.getId().toString().substring(0, 8));
            message.setText(buildOrderConfirmationText(order));

            mailSender.send(message);
            log.info("Order confirmation email sent to: {}", order.getBuyer().getEmail());
        } catch (Exception e) {
            log.error("Failed to send order confirmation email", e);
        }
    }

    private String buildOrderConfirmationText(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("Dear ").append(order.getBuyer().getFullName()).append(",\n\n");
        sb.append("Thank you for your purchase! Your order has been confirmed.\n\n");
        sb.append("Order Details:\n");
        sb.append("Order ID: ").append(order.getId().toString().substring(0, 8)).append("\n");
        sb.append("Total: $").append(order.getTotal()).append("\n");
        sb.append("Date: ").append(order.getCreatedAt().toLocalDate()).append("\n\n");

        sb.append("Items purchased:\n");
        for (OrderItem item : order.getOrderItems()) {
            sb.append("- ").append(item.getWallpaper().getTitle())
                    .append(" ($").append(item.getPrice()).append(")\n");
        }

        sb.append("\nDownload Links:\n");
        List<Download> downloads = downloadRepository.findByOrderId(order.getId());
        for (Download download : downloads) {
            sb.append("- ").append(download.getWallpaper().getTitle())
                    .append(": ").append(frontendUrl).append("/download/").append(download.getToken()).append("\n");
        }

        sb.append("\nYour download links will be valid for 30 days.\n\n");
        sb.append("Thank you for choosing Mana Wallpapers!\n\n");
        sb.append("Best regards,\n");
        sb.append("The Mana Wallpapers Team");

        return sb.toString();
    }

    public void sendWelcomeEmail(String email, String fullName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Welcome to Mana Wallpapers!");
            message.setText(buildWelcomeText(fullName));

            mailSender.send(message);
            log.info("Welcome email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send welcome email", e);
        }
    }

    private String buildWelcomeText(String fullName) {
        return String.format(
                "Dear %s,\n\n" +
                        "Welcome to Mana Wallpapers! We're excited to have you join our community.\n\n" +
                        "Start exploring our collection of premium wallpapers at %s\n\n" +
                        "Best regards,\n" +
                        "The Mana Wallpapers Team",
                fullName, frontendUrl
        );
    }
}