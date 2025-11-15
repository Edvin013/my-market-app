package ru.mirakyan.mymarket.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mirakyan.mymarket.dto.ItemDto;
import ru.mirakyan.mymarket.dto.OrderDto;
import ru.mirakyan.mymarket.exception.EmptyCartException;
import ru.mirakyan.mymarket.exception.OrderNotFoundException;
import ru.mirakyan.mymarket.mapper.ItemDtoMapper;
import ru.mirakyan.mymarket.model.CartItem;
import ru.mirakyan.mymarket.model.Order;
import ru.mirakyan.mymarket.model.OrderItem;
import ru.mirakyan.mymarket.repository.CartItemRepository;
import ru.mirakyan.mymarket.repository.OrderRepository;
import ru.mirakyan.mymarket.service.CartService;
import ru.mirakyan.mymarket.service.OrderService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final ItemDtoMapper itemDtoMapper;

    @Override
    @Transactional
    public Long createOrder() {
        List<CartItem> cartItems = cartItemRepository.findAll();
        if (cartItems.isEmpty()) {
            throw new EmptyCartException();
        }

        Long totalSum = cartItems.stream()
                .mapToLong(ci -> ci.getItem().getPrice() * ci.getCount())
                .sum();

        Order order = new Order(totalSum);
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem(
                    cartItem.getItem(),
                    cartItem.getCount(),
                    cartItem.getItem().getPrice()
            );
            order.addItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart();
        return savedOrder.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return convertToDto(order);
    }

    private OrderDto convertToDto(Order order) {
        List<ItemDto> itemDtos = order.getItems().stream()
                .map(itemDtoMapper::fromOrderItem)
                .collect(Collectors.toList());
        return new OrderDto(order.getId(), itemDtos, order.getTotalSum());
    }
}

