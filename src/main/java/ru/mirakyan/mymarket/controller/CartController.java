package ru.mirakyan.mymarket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mirakyan.mymarket.dto.ItemDto;
import ru.mirakyan.mymarket.enums.ItemAction;
import ru.mirakyan.mymarket.service.CartService;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/items")
    public String getCartItems(Model model) {
        List<ItemDto> items = cartService.getCartItems();
        Long total = cartService.getTotalPrice();

        model.addAttribute("items", items);
        model.addAttribute("total", total);

        return "cart";
    }

    @PostMapping("/items")
    public String updateCartItem(
            @RequestParam Long id,
            @RequestParam ItemAction action,
            Model model) {

        cartService.updateCartItem(id, action);

        List<ItemDto> items = cartService.getCartItems();
        Long total = cartService.getTotalPrice();

        model.addAttribute("items", items);
        model.addAttribute("total", total);

        return "cart";
    }
}

