package ru.mirakyan.mymarket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mirakyan.mymarket.dto.ItemDto;
import ru.mirakyan.mymarket.dto.PagingDto;
import ru.mirakyan.mymarket.enums.ItemAction;
import ru.mirakyan.mymarket.enums.SortType;
import ru.mirakyan.mymarket.mapper.ItemDtoMapper;
import ru.mirakyan.mymarket.model.CartItem;
import ru.mirakyan.mymarket.model.Item;
import ru.mirakyan.mymarket.repository.CartItemRepository;
import ru.mirakyan.mymarket.repository.ItemRepository;
import ru.mirakyan.mymarket.service.impl.ItemServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartService cartService;

    @Spy
    private ItemDtoMapper itemDtoMapper = new ItemDtoMapper();

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    void setUp() {
        item1 = new Item(1L, "Laptop", "Gaming laptop", "/img1.jpg", 1000L);
        item2 = new Item(2L, "Mouse", "Gaming mouse", "/img2.jpg", 50L);
        item3 = new Item(3L, "Keyboard", "Mechanical keyboard", "/img3.jpg", 150L);
    }

    @Test
    void testGetItems_WithoutSearchAndSort() {
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2, item3));
        when(cartItemRepository.findAll()).thenReturn(List.of());

        List<List<ItemDto>> result = itemService.getItems(null, null, 1, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(itemRepository).findAll();
    }

    @Test
    void testGetItems_WithSearch() {
        when(itemRepository.findByTitleOrDescriptionContaining("gaming"))
                .thenReturn(Arrays.asList(item1, item2));
        when(cartItemRepository.findAll()).thenReturn(List.of());

        List<List<ItemDto>> result = itemService.getItems("gaming", null, 1, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(itemRepository).findByTitleOrDescriptionContaining("gaming");
    }

    @Test
    void testGetItems_WithAlphaSort() {
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2, item3));
        when(cartItemRepository.findAll()).thenReturn(List.of());

        List<List<ItemDto>> result = itemService.getItems(null, SortType.ALPHA, 1, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Keyboard", result.get(0).get(0).getTitle());
    }

    @Test
    void testGetItems_WithPriceSort() {
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2, item3));
        when(cartItemRepository.findAll()).thenReturn(List.of());

        List<List<ItemDto>> result = itemService.getItems(null, SortType.PRICE, 1, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Mouse", result.get(0).get(0).getTitle());
    }

    @Test
    void testGetItems_WithPagination() {
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2, item3));
        when(cartItemRepository.findAll()).thenReturn(List.of());

        List<List<ItemDto>> result = itemService.getItems(null, null, 1, 2);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetItemById() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(cartItemRepository.findByItem(item1)).thenReturn(Optional.empty());

        ItemDto result = itemService.getItemById(1L);

        assertNotNull(result);
        assertEquals("Laptop", result.getTitle());
        assertEquals(1000L, result.getPrice());
        assertEquals(0, result.getCount());
        verify(itemRepository).findById(1L);
    }

    @Test
    void testGetItemById_WithCartCount() {
        CartItem cartItem = new CartItem(item1, 3);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(cartItemRepository.findByItem(item1)).thenReturn(Optional.of(cartItem));

        ItemDto result = itemService.getItemById(1L);

        assertNotNull(result);
        assertEquals("Laptop", result.getTitle());
        assertEquals(3, result.getCount());
    }

    @Test
    void testGetItemById_NotFound() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> itemService.getItemById(999L));
    }

    @Test
    void testUpdateCartItem_PlusDelegatesToCartService() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        itemService.updateCartItem(1L, ItemAction.PLUS);
        verify(cartService).updateCartItem(eq(1L), eq(ItemAction.PLUS));
    }

    @Test
    void testUpdateCartItem_MinusDelegatesToCartService() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        itemService.updateCartItem(1L, ItemAction.MINUS);
        verify(cartService).updateCartItem(eq(1L), eq(ItemAction.MINUS));
    }

    @Test
    void testUpdateCartItem_NotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> itemService.updateCartItem(1L, ItemAction.PLUS));
        verify(cartService, never()).updateCartItem(anyLong(), any());
    }

    @Test
    void testGetPagingInfo_WithoutSearch() {
        when(itemRepository.count()).thenReturn(10L);

        PagingDto result = itemService.getPagingInfo(null, 2, 3);

        assertNotNull(result);
        assertEquals(3, result.getPageSize());
        assertEquals(2, result.getPageNumber());
        assertTrue(result.isHasPrevious());
        assertTrue(result.isHasNext());
    }

    @Test
    void testGetPagingInfo_WithSearch() {
        when(itemRepository.findByTitleOrDescriptionContaining("gaming"))
                .thenReturn(Arrays.asList(item1, item2));

        PagingDto result = itemService.getPagingInfo("gaming", 1, 3);

        assertNotNull(result);
        assertEquals(1, result.getPageNumber());
        assertFalse(result.isHasPrevious());
        assertFalse(result.isHasNext());
    }

    @Test
    void testGetPagingInfo_FirstPage() {
        when(itemRepository.count()).thenReturn(10L);

        PagingDto result = itemService.getPagingInfo(null, 1, 3);

        assertFalse(result.isHasPrevious());
        assertTrue(result.isHasNext());
    }

    @Test
    void testGetPagingInfo_LastPage() {
        when(itemRepository.count()).thenReturn(9L);

        PagingDto result = itemService.getPagingInfo(null, 3, 3);

        assertTrue(result.isHasPrevious());
        assertFalse(result.isHasNext());
    }
}
