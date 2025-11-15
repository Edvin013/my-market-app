package ru.mirakyan.mymarket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mirakyan.mymarket.dto.ItemDto;
import ru.mirakyan.mymarket.dto.OrderDto;
import ru.mirakyan.mymarket.mapper.ItemDtoMapper;
import ru.mirakyan.mymarket.model.CartItem;
import ru.mirakyan.mymarket.model.Item;
import ru.mirakyan.mymarket.model.Order;
import ru.mirakyan.mymarket.model.OrderItem;
import ru.mirakyan.mymarket.repository.CartItemRepository;
import ru.mirakyan.mymarket.repository.OrderRepository;
import ru.mirakyan.mymarket.service.impl.OrderServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartService cartService;

    @Spy
    private ItemDtoMapper itemDtoMapper = new ItemDtoMapper();

    @InjectMocks
    private OrderServiceImpl orderService;

    private Item item1;
    private Item item2;
    private CartItem cartItem1;
    private CartItem cartItem2;
    private Order order;

    @BeforeEach
    void setUp() {
        item1 = new Item(1L, "Ball", "Red ball", "/images/ball.jpg", 100L);
        item2 = new Item(2L, "Apple", "Fresh apple", "/images/apple.jpg", 50L);

        cartItem1 = new CartItem(item1, 2);
        cartItem2 = new CartItem(item2, 3);

        order = new Order(350L);
        order.setId(1L);
        OrderItem orderItem1 = new OrderItem(item1, 2, 100L);
        OrderItem orderItem2 = new OrderItem(item2, 3, 50L);
        order.addItem(orderItem1);
        order.addItem(orderItem2);
    }

    @Test
    void createOrder_shouldCreateOrderFromCart() {
        when(cartItemRepository.findAll()).thenReturn(Arrays.asList(cartItem1, cartItem2));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Long orderId = orderService.createOrder();

        assertNotNull(orderId);
        assertEquals(1L, orderId);
        verify(orderRepository).save(any(Order.class));
        verify(cartService).clearCart();
    }

    @Test
    void createOrder_shouldThrowException_whenCartIsEmpty() {
        when(cartItemRepository.findAll()).thenReturn(Arrays.asList());

        assertThrows(RuntimeException.class, () -> orderService.createOrder());
        verify(orderRepository, never()).save(any(Order.class));
        verify(cartService, never()).clearCart();
    }

    @Test
    void createOrder_shouldCalculateCorrectTotal() {
        when(cartItemRepository.findAll()).thenReturn(Arrays.asList(cartItem1, cartItem2));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            assertEquals(350L, savedOrder.getTotalSum());
            savedOrder.setId(1L);
            return savedOrder;
        });

        orderService.createOrder();

        verify(orderRepository).save(argThat(o -> o.getTotalSum() == 350L));
    }

    @Test
    void getAllOrders_shouldReturnAllOrders() {
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order));

        List<OrderDto> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(350L, result.get(0).getTotalSum());
        assertEquals(2, result.get(0).getItems().size());
    }

    @Test
    void getOrderById_shouldReturnOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDto result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(350L, result.getTotalSum());
        assertEquals(2, result.getItems().size());
    }

    @Test
    void getOrderById_shouldThrowException_whenOrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.getOrderById(99L));
    }

    @Test
    void getOrderById_shouldReturnCorrectItemDtos() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDto result = orderService.getOrderById(1L);

        List<ItemDto> items = result.getItems();
        assertEquals(2, items.size());

        ItemDto itemDto1 = items.get(0);
        assertEquals(1L, itemDto1.getId());
        assertEquals("Ball", itemDto1.getTitle());
        assertEquals(100L, itemDto1.getPrice());
        assertEquals(2, itemDto1.getCount());

        ItemDto itemDto2 = items.get(1);
        assertEquals(2L, itemDto2.getId());
        assertEquals("Apple", itemDto2.getTitle());
        assertEquals(50L, itemDto2.getPrice());
        assertEquals(3, itemDto2.getCount());
    }
}
