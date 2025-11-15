package ru.mirakyan.mymarket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mirakyan.mymarket.dto.ItemDto;
import ru.mirakyan.mymarket.enums.ItemAction;
import ru.mirakyan.mymarket.service.CartService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Test
    void testGetCartItems() throws Exception {
        ItemDto item = new ItemDto(1L, "Test", "Desc", "/img", 100L, 2);

        when(cartService.getCartItems()).thenReturn(List.of(item));
        when(cartService.getTotalPrice()).thenReturn(200L);

        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("total"));
    }

    @Test
    void testUpdateCartItem() throws Exception {
        ItemDto item = new ItemDto(1L, "Test", "Desc", "/img", 100L, 2);

        when(cartService.getCartItems()).thenReturn(List.of(item));
        when(cartService.getTotalPrice()).thenReturn(200L);
        doNothing().when(cartService).updateCartItem(anyLong(), any(ItemAction.class));

        mockMvc.perform(post("/cart/items")
                        .param("id", "1")
                        .param("action", "PLUS"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("total"));

        verify(cartService, times(1)).updateCartItem(1L, ItemAction.PLUS);
    }
}

