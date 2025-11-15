package ru.mirakyan.mymarket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mirakyan.mymarket.dto.ItemDto;
import ru.mirakyan.mymarket.dto.PagingDto;
import ru.mirakyan.mymarket.enums.ItemAction;
import ru.mirakyan.mymarket.enums.SortType;
import ru.mirakyan.mymarket.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Test
    void testGetItems() throws Exception {
        ItemDto item = new ItemDto(1L, "Test", "Desc", "/img", 100L, 0);
        List<List<ItemDto>> items = List.of(List.of(item));
        PagingDto paging = new PagingDto(5, 1, false, false);

        when(itemService.getItems(anyString(), any(SortType.class), anyInt(), anyInt())).thenReturn(items);
        when(itemService.getPagingInfo(anyString(), anyInt(), anyInt())).thenReturn(paging);

        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("sort"))
                .andExpect(model().attributeExists("paging"));
    }

    @Test
    void testGetItemById() throws Exception {
        ItemDto item = new ItemDto(1L, "Test", "Desc", "/img", 100L, 0);

        when(itemService.getItemById(1L)).thenReturn(item);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"));
    }

    @Test
    void testUpdateItemInCartFromItemsPage() throws Exception {
        doNothing().when(itemService).updateCartItem(anyLong(), any(ItemAction.class));

        mockMvc.perform(post("/items")
                        .param("id", "1")
                        .param("action", "PLUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/items?*"));

        verify(itemService, times(1)).updateCartItem(1L, ItemAction.PLUS);
    }

    @Test
    void testUpdateItemInCartFromItemPage() throws Exception {
        ItemDto item = new ItemDto(1L, "Test", "Desc", "/img", 100L, 1);

        doNothing().when(itemService).updateCartItem(anyLong(), any(ItemAction.class));
        when(itemService.getItemById(1L)).thenReturn(item);

        mockMvc.perform(post("/items/1")
                        .param("action", "PLUS"))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"));

        verify(itemService, times(1)).updateCartItem(1L, ItemAction.PLUS);
    }
}

