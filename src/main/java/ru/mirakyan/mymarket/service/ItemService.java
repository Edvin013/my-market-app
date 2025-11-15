package ru.mirakyan.mymarket.service;

import ru.mirakyan.mymarket.dto.ItemDto;
import ru.mirakyan.mymarket.dto.PagingDto;
import ru.mirakyan.mymarket.enums.ItemAction;
import ru.mirakyan.mymarket.enums.SortType;

import java.util.List;

public interface ItemService {
    List<List<ItemDto>> getItems(String search, SortType sort, int pageNumber, int pageSize);
    ItemDto getItemById(Long id);
    void updateCartItem(Long itemId, ItemAction action);
    PagingDto getPagingInfo(String search, int pageNumber, int pageSize);
}
