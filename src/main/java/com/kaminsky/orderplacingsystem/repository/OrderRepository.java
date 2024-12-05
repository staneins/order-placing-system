package com.kaminsky.orderplacingsystem.repository;

import com.kaminsky.orderplacingsystem.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
