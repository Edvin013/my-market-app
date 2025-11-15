package ru.mirakyan.mymarket;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import ru.mirakyan.mymarket.controller.CartController;
import ru.mirakyan.mymarket.controller.ItemController;
import ru.mirakyan.mymarket.controller.OrderController;
import ru.mirakyan.mymarket.service.CartService;
import ru.mirakyan.mymarket.service.ItemService;
import ru.mirakyan.mymarket.service.OrderService;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class MyMarketApplicationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertNotNull(context);
    }

    @Test
    void controllersAreLoaded() {
        assertNotNull(context.getBean(ItemController.class));
        assertNotNull(context.getBean(CartController.class));
        assertNotNull(context.getBean(OrderController.class));
    }

    @Test
    void servicesAreLoaded() {
        assertNotNull(context.getBean(ItemService.class));
        assertNotNull(context.getBean(CartService.class));
        assertNotNull(context.getBean(OrderService.class));
    }
}

