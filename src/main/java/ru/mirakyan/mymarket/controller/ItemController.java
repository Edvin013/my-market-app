package ru.mirakyan.mymarket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mirakyan.mymarket.dto.ItemDto;
import ru.mirakyan.mymarket.dto.PagingDto;
import ru.mirakyan.mymarket.enums.ItemAction;
import ru.mirakyan.mymarket.enums.SortType;
import ru.mirakyan.mymarket.service.ItemService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;


    @GetMapping({"/", "/items"})
    public String getItems(
            @RequestParam(required = false , defaultValue = "") String search,
            @RequestParam(defaultValue = "NO") SortType sort,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize,
            Model model) {

        List<List<ItemDto>> items = itemService.getItems(search, sort, pageNumber, pageSize);
        PagingDto paging = itemService.getPagingInfo(search, pageNumber, pageSize);

        model.addAttribute("items", items);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("paging", paging);

        return "items";
    }

    @PostMapping("/items")
    public String updateCartFromItems(
            @RequestParam Long id,
            @RequestParam ItemAction action,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "NO") SortType sort,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize) {

        itemService.updateCartItem(id, action);

        StringBuilder redirectUrl = new StringBuilder("redirect:/items?");
        if (search != null && !search.isEmpty()) {
            redirectUrl.append("search=").append(search).append("&");
        }
        redirectUrl.append("sort=").append(sort)
                   .append("&pageNumber=").append(pageNumber)
                   .append("&pageSize=").append(pageSize);

        return redirectUrl.toString();
    }

    @GetMapping("/items/{id}")
    public String getItem(@PathVariable Long id, Model model) {
        ItemDto item = itemService.getItemById(id);
        model.addAttribute("item", item);
        return "item";
    }

    @PostMapping("/items/{id}")
    public String updateCartFromItem(
            @PathVariable Long id,
            @RequestParam ItemAction action,
            Model model) {

        itemService.updateCartItem(id, action);
        ItemDto item = itemService.getItemById(id);
        model.addAttribute("item", item);

        return "item";
    }
}
