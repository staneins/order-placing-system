package com.kaminsky.orderplacingsystem.repository;

import com.kaminsky.orderplacingsystem.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
