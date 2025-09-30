package com.manawallpapers.repository;

import com.manawallpapers.entity.Order;
import com.manawallpapers.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByBuyer(User buyer, Pageable pageable);
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
}