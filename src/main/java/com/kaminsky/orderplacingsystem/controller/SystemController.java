package com.kaminsky.orderplacingsystem.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaminsky.orderplacingsystem.entity.Order;
import com.kaminsky.orderplacingsystem.entity.Product;
import com.kaminsky.orderplacingsystem.exception.JsonException;
import com.kaminsky.orderplacingsystem.repository.OrderRepository;
import com.kaminsky.orderplacingsystem.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/")
public class SystemController {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper mapper;

    public SystemController(ProductRepository productRepository, OrderRepository orderRepository, ObjectMapper mapper) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.mapper = mapper;
    }


    @GetMapping
    public String getAllProducts() {
        try {
            return mapper.writeValueAsString(productRepository.findAll());
        } catch (JsonProcessingException e) {
            throw new JsonException();
        }
    }

    @GetMapping("{id}")
    public String getProductById(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            try {
                return mapper.writeValueAsString(product.get());
            } catch (JsonProcessingException e) {
                throw new JsonException();
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Продукт не найден");
    }

    @PostMapping
    public void createProduct(@RequestBody String productJson) {
        try {
            productRepository.save(mapper.readValue(productJson, Product.class));
        } catch (JsonProcessingException e) {
            throw new JsonException();
        }
    }

    @PutMapping("{id}")
    public void updateProduct(@RequestBody String productJson, @PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Продукт не найден");
        }
        Product product;
        try {
            product = mapper.readValue(productJson, Product.class);
            product.setProductId(id);
            productRepository.save(product);
        } catch (JsonProcessingException e) {
            throw new JsonException();
        }
    }

    @DeleteMapping("{id}")
    public void deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
    }

    @PostMapping("orders")
    public void createOrder(@RequestBody String orderJson) {
        try {
            orderRepository.save(mapper.readValue(orderJson, Order.class));
        } catch (JsonProcessingException e) {
            throw new JsonException();
        }
    }

    @GetMapping("orders/{id}")
    public String getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            try {
                return mapper.writeValueAsString(order.get());
            } catch (JsonProcessingException e) {
                throw new JsonException();
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ не найден");
    }
}
