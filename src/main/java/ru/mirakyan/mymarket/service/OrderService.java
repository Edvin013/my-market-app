package ru.mirakyan.mymarket.service;

import ru.mirakyan.mymarket.dto.OrderDto;

import java.util.List;

public interface OrderService {
    Long createOrder();
    List<OrderDto> getAllOrders();
    OrderDto getOrderById(Long id);
}
