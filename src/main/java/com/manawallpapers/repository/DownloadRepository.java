package com.manawallpapers.repository;

import com.manawallpapers.entity.Download;
import com.manawallpapers.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DownloadRepository extends JpaRepository<Download, UUID> {
    Optional<Download> findByToken(String token);
    List<Download> findByBuyer(User buyer);

    @Query("SELECT d FROM Download d WHERE d.order.id = :orderId")
    List<Download> findByOrderId(@Param("orderId") UUID orderId);
}