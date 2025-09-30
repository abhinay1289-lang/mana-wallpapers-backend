package com.manawallpapers.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallpaper_id", nullable = false)
    private Wallpaper wallpaper;

    @NotNull
    @Column(name = "price_cents", nullable = false)
    private Integer priceCents;

    @NotNull
    @Column(nullable = false)
    private Integer quantity = 1;

    public BigDecimal getPrice() {
        return BigDecimal.valueOf(priceCents).divide(BigDecimal.valueOf(100));
    }

    public void setPrice(BigDecimal price) {
        this.priceCents = price.multiply(BigDecimal.valueOf(100)).intValue();
    }
}