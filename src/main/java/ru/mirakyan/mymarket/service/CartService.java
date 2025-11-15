package ru.mirakyan.mymarket.service;

import ru.mirakyan.mymarket.dto.ItemDto;
import ru.mirakyan.mymarket.enums.ItemAction;

import java.util.List;

public interface CartService {
    List<ItemDto> getCartItems();
    Long getTotalPrice();
    void updateCartItem(Long itemId, ItemAction action);
    void clearCart();
}
