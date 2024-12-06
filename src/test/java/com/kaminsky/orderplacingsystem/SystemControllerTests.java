package com.kaminsky.orderplacingsystem;

import com.kaminsky.orderplacingsystem.controller.SystemController;
import com.kaminsky.orderplacingsystem.entity.Order;
import com.kaminsky.orderplacingsystem.entity.Product;
import com.kaminsky.orderplacingsystem.entity.Status;
import com.kaminsky.orderplacingsystem.repository.OrderRepository;
import com.kaminsky.orderplacingsystem.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SystemController.class)
public class SystemControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    OrderRepository orderRepository;

    @MockBean
    ProductRepository productRepository;

    @BeforeEach
    public void setUp() {
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setName("Product 1");
        product1.setDescription("Description 1");
        product1.setPrice(100);
        product1.setQuantityInStock(50);

        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setName("Product 2");
        product2.setDescription("Description 2");
        product2.setPrice(150);
        product2.setQuantityInStock(30);

        Product product3 = new Product();
        product3.setProductId(3L);
        product3.setName("Product 3");
        product3.setDescription("Description 3");
        product3.setPrice(200);
        product3.setQuantityInStock(20);

        Order order1 = new Order();
        order1.setOrderId(1L);
        order1.setOrderDate(LocalDateTime.of(2022, 2,22, 22, 22, 22));
        order1.setOrderStatus(Status.IN_PROGRESS);

        List<Product> products = Arrays.asList(product1, product2, product3);

        when(productRepository.findAll()).thenReturn(products);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.existsById(1L)).thenReturn(true);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(orderRepository.existsById(1L)).thenReturn(true);
    }

    @Test
    public void testProductGetAll() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(jsonPath("$[0].productId").value(1L))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[1].productId").value(2L))
                .andExpect(jsonPath("$[1].name").value("Product 2"))
                .andExpect(jsonPath("$[2].productId").value(3L))
                .andExpect(jsonPath("$[2].name").value("Product 3"));
    }

    @Test
    public void testProductGetById() throws Exception {
        mockMvc.perform(get("/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(jsonPath("$.productId").value(1L))
                .andExpect(jsonPath("$.name").value("Product 1"))
                .andExpect(jsonPath("$.description").value("Description 1"))
                .andExpect(jsonPath("$.price").value(100))
                .andExpect(jsonPath("$.quantityInStock").value(50));
    }

    @Test
    public void testOrderGetById() throws Exception {
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.orderDate").value("2022-02-22T22:22:22"))
                .andExpect(jsonPath("$.orderStatus").value("IN_PROGRESS"));
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/1"))
                .andExpect(status().isOk());

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testProductPost() throws Exception {
        String newProductJson = """
                {
                    "name": "Product 4",
                    "description": "Description 4",
                    "price": 250,
                    "quantityInStock": 40
                }
                """;

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newProductJson))
                .andExpect(status().isOk());

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();
        assertEquals("Product 4", savedProduct.getName());
        assertEquals("Description 4", savedProduct.getDescription());
        assertEquals(250, savedProduct.getPrice());
        assertEquals(40, savedProduct.getQuantityInStock());
    }

    @Test
    public void testOrderPost() throws Exception {
        String newOrderJson = """
                {
                    "orderId": 1,
                    "orderDate": "2022-02-22T22:22:22",
                    "orderStatus": "IN_PROGRESS"
                }
                """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newOrderJson))
                .andExpect(status().isOk());

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertEquals(1L, savedOrder.getOrderId());
        assertEquals(LocalDateTime.of(2022, 2,22, 22, 22, 22), savedOrder.getOrderDate());
        assertEquals(Status.IN_PROGRESS, savedOrder.getOrderStatus());
    }

    @Test
    public void testPut() throws Exception {
        String updatedProductJson = """
                {
                    "name": "Product",
                    "description": "Description",
                    "price": 300,
                    "quantityInStock": 60
                }
                """;

        mockMvc.perform(put("/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedProductJson))
                .andExpect(status().isOk());

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(productCaptor.capture());

        Product updatedProduct = productCaptor.getValue();
        assertEquals("Product", updatedProduct.getName());
        assertEquals("Description", updatedProduct.getDescription());
        assertEquals(300, updatedProduct.getPrice());
        assertEquals(60, updatedProduct.getQuantityInStock());
    }
}
