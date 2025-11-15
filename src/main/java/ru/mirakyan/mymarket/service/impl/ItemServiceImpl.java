package ru.mirakyan.mymarket.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mirakyan.mymarket.dto.ItemDto;
import ru.mirakyan.mymarket.dto.PagingDto;
import ru.mirakyan.mymarket.enums.ItemAction;
import ru.mirakyan.mymarket.enums.SortType;
import ru.mirakyan.mymarket.exception.ItemNotFoundException;
import ru.mirakyan.mymarket.mapper.ItemDtoMapper;
import ru.mirakyan.mymarket.model.CartItem;
import ru.mirakyan.mymarket.model.Item;
import ru.mirakyan.mymarket.repository.CartItemRepository;
import ru.mirakyan.mymarket.repository.ItemRepository;
import ru.mirakyan.mymarket.service.CartService;
import ru.mirakyan.mymarket.service.ItemService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemDtoMapper itemDtoMapper;
    private final CartService cartService;

    @Override
    @Transactional(readOnly = true)
    public List<List<ItemDto>> getItems(String search, SortType sort, int pageNumber, int pageSize) {
        List<Item> items = (!search.isEmpty())
                ? itemRepository.findByTitleOrDescriptionContaining(search)
                : itemRepository.findAll();

        Map<Long, Integer> cartCounts = getCartCounts();

        List<ItemDto> itemDtos = items.stream()
                .sorted(getComparator(sort))
                .map(item -> itemDtoMapper.fromItem(item, cartCounts.getOrDefault(item.getId(), 0)))
                .collect(Collectors.toList());

        int startIndex = Math.max(0, (pageNumber - 1) * pageSize);
        if (startIndex >= itemDtos.size()) {
            return Collections.emptyList();
        }
        int endIndex = Math.min(startIndex + pageSize, itemDtos.size());
        List<ItemDto> pagedItems = itemDtos.subList(startIndex, endIndex);

        return groupByThreeWithPlaceholders(pagedItems);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));

        int cartCount = cartItemRepository.findByItem(item)
                .map(CartItem::getCount)
                .orElse(0);

        return itemDtoMapper.fromItem(item, cartCount);
    }

    @Override
    @Transactional
    public void updateCartItem(Long itemId, ItemAction action) {
        itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        cartService.updateCartItem(itemId, action);
    }

    @Override
    @Transactional(readOnly = true)
    public PagingDto getPagingInfo(String search, int pageNumber, int pageSize) {
        long totalItems = (search != null && !search.isEmpty())
                ? itemRepository.findByTitleOrDescriptionContaining(search).size()
                : itemRepository.count();

        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        boolean hasPrevious = pageNumber > 1;
        boolean hasNext = pageNumber < totalPages;

        return new PagingDto(pageSize, pageNumber, hasPrevious, hasNext);
    }

    private Comparator<Item> getComparator(SortType sort) {
        if (sort == SortType.ALPHA) {
            return Comparator.comparing(Item::getTitle);
        } else if (sort == SortType.PRICE) {
            return Comparator.comparing(Item::getPrice);
        }
        return (a, b) -> 0;
    }

    private Map<Long, Integer> getCartCounts() {
        return cartItemRepository.findAll().stream()
                .collect(Collectors.toMap(ci -> ci.getItem().getId(), CartItem::getCount));
    }

    private List<List<ItemDto>> groupByThreeWithPlaceholders(List<ItemDto> items) {
        List<List<ItemDto>> grouped = new ArrayList<>();
        for (int i = 0; i < items.size(); i += 3) {
            List<ItemDto> row = new ArrayList<>(3);
            for (int j = 0; j < 3; j++) {
                int idx = i + j;
                if (idx < items.size()) {
                    row.add(items.get(idx));
                } else {
                    row.add(new ItemDto(-1L, "", "", "", 0L, 0));
                }
            }
            grouped.add(row);
        }
        return grouped;
    }
}

