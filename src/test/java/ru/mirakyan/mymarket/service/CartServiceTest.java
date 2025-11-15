package ru.mirakyan.mymarket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mirakyan.mymarket.dto.ItemDto;
import ru.mirakyan.mymarket.enums.ItemAction;
import ru.mirakyan.mymarket.mapper.ItemDtoMapper;
import ru.mirakyan.mymarket.model.CartItem;
import ru.mirakyan.mymarket.model.Item;
import ru.mirakyan.mymarket.repository.CartItemRepository;
import ru.mirakyan.mymarket.repository.ItemRepository;
import ru.mirakyan.mymarket.service.impl.CartServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ItemRepository itemRepository;

    @Spy
    private ItemDtoMapper itemDtoMapper = new ItemDtoMapper();

    @InjectMocks
    private CartServiceImpl cartService;

    private Item item1;
    private Item item2;
    private CartItem cartItem1;
    private CartItem cartItem2;

    @BeforeEach
    void setUp() {
        item1 = new Item(1L, "Ball", "Red ball", "/images/ball.jpg", 100L);
        item2 = new Item(2L, "Book", "Interesting book", "/images/book.jpg", 200L);
        cartItem1 = new CartItem(item1, 2);
        cartItem2 = new CartItem(item2, 1);
    }

    @Test
    void getCartItems_shouldReturnEmptyList_whenCartIsEmpty() {
        when(cartItemRepository.findAll()).thenReturn(Arrays.asList());

        List<ItemDto> result = cartService.getCartItems();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cartItemRepository).findAll();
    }

    @Test
    void getCartItems_shouldReturnCartItems() {
        when(cartItemRepository.findAll()).thenReturn(Arrays.asList(cartItem1, cartItem2));

        List<ItemDto> result = cartService.getCartItems();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Ball", result.get(0).getTitle());
        assertEquals(2, result.get(0).getCount());
        assertEquals("Book", result.get(1).getTitle());
        assertEquals(1, result.get(1).getCount());
    }

    @Test
    void getTotalPrice_shouldReturnZero_whenCartIsEmpty() {
        when(cartItemRepository.findAll()).thenReturn(Arrays.asList());

        Long result = cartService.getTotalPrice();

        assertEquals(0L, result);
    }

    @Test
    void getTotalPrice_shouldCalculateCorrectTotal() {
        when(cartItemRepository.findAll()).thenReturn(Arrays.asList(cartItem1, cartItem2));

        Long result = cartService.getTotalPrice();

        assertEquals(400L, result); // 2*100 + 1*200
    }

    @Test
    void updateCartItem_shouldAddNewItem_whenActionIsPlus() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(cartItemRepository.findByItem(item1)).thenReturn(Optional.empty());

        cartService.updateCartItem(1L, ItemAction.PLUS);

        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void updateCartItem_shouldIncreaseCount_whenItemExists() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(cartItemRepository.findByItem(item1)).thenReturn(Optional.of(cartItem1));

        cartService.updateCartItem(1L, ItemAction.PLUS);

        assertEquals(3, cartItem1.getCount());
        verify(cartItemRepository).save(cartItem1);
    }

    @Test
    void updateCartItem_shouldDecreaseCount_whenActionIsMinus() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(cartItemRepository.findByItem(item1)).thenReturn(Optional.of(cartItem1));

        cartService.updateCartItem(1L, ItemAction.MINUS);

        assertEquals(1, cartItem1.getCount());
        verify(cartItemRepository).save(cartItem1);
    }

    @Test
    void updateCartItem_shouldDeleteItem_whenActionIsDelete() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(cartItemRepository.findByItem(item1)).thenReturn(Optional.of(cartItem1));

        cartService.updateCartItem(1L, ItemAction.DELETE);

        verify(cartItemRepository).delete(cartItem1);
    }

    @Test
    void clearCart_shouldDeleteAllItems() {
        cartService.clearCart();

        verify(cartItemRepository).deleteAll();
    }
}
