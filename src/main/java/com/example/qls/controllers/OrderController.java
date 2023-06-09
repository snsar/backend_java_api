package com.example.qls.controllers;

import com.example.qls.models.Book;
import com.example.qls.models.Order;
import com.example.qls.models.User;
import com.example.qls.repositories.BookRepository;
import com.example.qls.repositories.OrderRepository;
import com.example.qls.repositories.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @PostMapping
    public ResponseEntity<Order> addOrder(@RequestBody OrderRequest orderRequest) {
        try {
            User user = userRepository.findById(orderRequest.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            Book book = bookRepository.findById(orderRequest.getBookId())
                    .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

            Order order = new Order();
            order.setUser(user);
            order.setBook(book);
            order.setQuantity(orderRequest.getQuantity());

            orderRepository.save(order);


            book.setSoldCount(book.getSoldCount() + order.getQuantity());
            bookRepository.save(book);

            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

            Book book = order.getBook();
            book.setSoldCount(book.getSoldCount() - order.getQuantity());
            bookRepository.save(book);

            orderRepository.deleteById(id);

            return ResponseEntity.noContent().build();
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Order not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Data
    public static class OrderRequest {
        private Long userId;
        private Long bookId;
        private int quantity;
    }
}
