package ru.mirakyan.mymarket.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.mirakyan.mymarket.model.Item;
import ru.mirakyan.mymarket.model.Order;
import ru.mirakyan.mymarket.model.OrderItem;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void testSaveOrder() {
        Item item = new Item(null, "Test Item", "Test Description", "/img", 100L);
        entityManager.persist(item);

        Order order = new Order();
        order.setTotalSum(300L);

        OrderItem orderItem = new OrderItem(item, 3, 100L);
        order.addItem(orderItem);

        Order saved = orderRepository.save(order);

        assertNotNull(saved.getId());
        assertEquals(300L, saved.getTotalSum());
        assertEquals(1, saved.getItems().size());
    }

    @Test
    void testFindById() {
        Item item = new Item(null, "Test Item", "Test Description", "/img", 100L);
        entityManager.persist(item);

        Order order = new Order();
        order.setTotalSum(200L);

        OrderItem orderItem = new OrderItem(item, 2, 100L);
        order.addItem(orderItem);

        entityManager.persist(order);
        entityManager.flush();

        Optional<Order> found = orderRepository.findById(order.getId());

        assertTrue(found.isPresent());
        assertEquals(200L, found.get().getTotalSum());
        assertEquals(1, found.get().getItems().size());
    }

    @Test
    void testFindAll() {
        Item item1 = new Item(null, "Item 1", "Description 1", "/img1", 100L);
        Item item2 = new Item(null, "Item 2", "Description 2", "/img2", 200L);
        entityManager.persist(item1);
        entityManager.persist(item2);

        Order order1 = new Order();
        order1.setTotalSum(100L);
        order1.addItem(new OrderItem(item1, 1, 100L));

        Order order2 = new Order();
        order2.setTotalSum(400L);
        order2.addItem(new OrderItem(item2, 2, 200L));

        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.flush();

        List<Order> orders = orderRepository.findAll();

        assertEquals(2, orders.size());
    }
}

