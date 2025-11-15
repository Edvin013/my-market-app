package ru.mirakyan.mymarket.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.mirakyan.mymarket.model.CartItem;
import ru.mirakyan.mymarket.model.Item;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CartItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    void testFindByItem() {
        Item item = new Item(null, "Test Item", "Test Description", "/img", 100L);
        entityManager.persist(item);

        CartItem cartItem = new CartItem(item, 3);
        entityManager.persist(cartItem);
        entityManager.flush();

        Optional<CartItem> found = cartItemRepository.findByItem(item);

        assertTrue(found.isPresent());
        assertEquals(3, found.get().getCount());
        assertEquals("Test Item", found.get().getItem().getTitle());
    }

    @Test
    void testFindByItem_NotFound() {
        Item item = new Item(null, "Test Item", "Test Description", "/img", 100L);
        entityManager.persist(item);
        entityManager.flush();

        Item anotherItem = new Item(null, "Another Item", "Another Description", "/img2", 200L);
        entityManager.persist(anotherItem);
        entityManager.flush();

        Optional<CartItem> found = cartItemRepository.findByItem(anotherItem);

        assertFalse(found.isPresent());
    }

    @Test
    void testSaveAndDelete() {
        Item item = new Item(null, "Test Item", "Test Description", "/img", 100L);
        entityManager.persist(item);

        CartItem cartItem = new CartItem(item, 1);
        CartItem saved = cartItemRepository.save(cartItem);

        assertNotNull(saved.getId());

        cartItemRepository.delete(saved);

        Optional<CartItem> found = cartItemRepository.findById(saved.getId());
        assertFalse(found.isPresent());
    }
}

