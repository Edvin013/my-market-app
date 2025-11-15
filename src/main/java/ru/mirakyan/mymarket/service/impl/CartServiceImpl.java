package ru.mirakyan.mymarket.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mirakyan.mymarket.dto.ItemDto;
import ru.mirakyan.mymarket.enums.ItemAction;
import ru.mirakyan.mymarket.exception.ItemNotFoundException;
import ru.mirakyan.mymarket.mapper.ItemDtoMapper;
import ru.mirakyan.mymarket.model.CartItem;
import ru.mirakyan.mymarket.model.Item;
import ru.mirakyan.mymarket.repository.CartItemRepository;
import ru.mirakyan.mymarket.repository.ItemRepository;
import ru.mirakyan.mymarket.service.CartService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final ItemDtoMapper itemDtoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getCartItems() {
        return cartItemRepository.findAll().stream()
                .map(itemDtoMapper::fromCartItem)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalPrice() {
        return cartItemRepository.findAll().stream()
                .mapToLong(ci -> ci.getItem().getPrice() * ci.getCount())
                .sum();
    }

    @Override
    @Transactional
    public void updateCartItem(Long itemId, ItemAction action) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        Optional<CartItem> cartItemOpt = cartItemRepository.findByItem(item);

        if (action == ItemAction.PLUS) {
            plus(cartItemOpt, item);
        } else if (action == ItemAction.MINUS) {
            minus(cartItemOpt);
        } else if (action == ItemAction.DELETE) {
            cartItemOpt.ifPresent(cartItemRepository::delete);
        }
    }

    private void minus(Optional<CartItem> cartItemOpt) {
        if (cartItemOpt.isEmpty()) {
            return;
        }
        CartItem cartItem = cartItemOpt.get();
        if (cartItem.getCount() > 1) {
            cartItem.setCount(cartItem.getCount() - 1);
            cartItemRepository.save(cartItem);
        } else {
            cartItemRepository.delete(cartItem);
        }
    }

    private void plus(Optional<CartItem> cartItemOpt, Item item) {
        if (cartItemOpt.isPresent()) {
            CartItem cartItem = cartItemOpt.get();
            cartItem.setCount(cartItem.getCount() + 1);
            cartItemRepository.save(cartItem);
        } else {
            cartItemRepository.save(new CartItem(item, 1));
        }
    }

    @Override
    @Transactional
    public void clearCart() {
        cartItemRepository.deleteAll();
    }
}

