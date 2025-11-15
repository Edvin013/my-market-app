package ru.mirakyan.mymarket.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.mirakyan.mymarket.model.Item;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void testSaveItem() {
        Item item = new Item(null, "Test Item", "Test Description", "/img/test.jpg", 100L);
        Item saved = itemRepository.save(item);

        assertNotNull(saved.getId());
        assertEquals("Test Item", saved.getTitle());
        assertEquals("Test Description", saved.getDescription());
        assertEquals("/img/test.jpg", saved.getImgPath());
        assertEquals(100L, saved.getPrice());
    }

    @Test
    void testFindById() {
        Item item = new Item(null, "Test Item", "Test Description", "/img/test.jpg", 100L);
        entityManager.persist(item);
        entityManager.flush();

        Optional<Item> found = itemRepository.findById(item.getId());

        assertTrue(found.isPresent());
        assertEquals("Test Item", found.get().getTitle());
        assertEquals(100L, found.get().getPrice());
    }

    @Test
    void testFindAll() {
        Item item1 = new Item(null, "Item 1", "Description 1", "/img1.jpg", 100L);
        Item item2 = new Item(null, "Item 2", "Description 2", "/img2.jpg", 200L);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.flush();

        List<Item> items = itemRepository.findAll();

        assertEquals(2, items.size());
    }

    @Test
    void testFindByTitleOrDescriptionContaining_SearchInTitle() {
        Item item1 = new Item(null, "Laptop", "Gaming laptop", "/img1.jpg", 1000L);
        Item item2 = new Item(null, "Mouse", "Gaming mouse", "/img2.jpg", 50L);
        Item item3 = new Item(null, "Keyboard", "Mechanical keyboard", "/img3.jpg", 150L);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
        entityManager.flush();

        List<Item> found = itemRepository.findByTitleOrDescriptionContaining("laptop");

        assertEquals(1, found.size());
        assertEquals("Laptop", found.get(0).getTitle());
    }

    @Test
    void testFindByTitleOrDescriptionContaining_SearchInDescription() {
        Item item1 = new Item(null, "Laptop", "Gaming laptop", "/img1.jpg", 1000L);
        Item item2 = new Item(null, "Mouse", "Gaming mouse", "/img2.jpg", 50L);
        Item item3 = new Item(null, "Keyboard", "Mechanical keyboard", "/img3.jpg", 150L);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
        entityManager.flush();

        List<Item> found = itemRepository.findByTitleOrDescriptionContaining("gaming");

        assertEquals(2, found.size());
    }

    @Test
    void testFindByTitleOrDescriptionContaining_CaseInsensitive() {
        Item item = new Item(null, "Laptop", "Gaming Laptop", "/img.jpg", 1000L);
        entityManager.persist(item);
        entityManager.flush();

        List<Item> found = itemRepository.findByTitleOrDescriptionContaining("LAPTOP");

        assertEquals(1, found.size());
        assertEquals("Laptop", found.get(0).getTitle());
    }

    @Test
    void testFindByTitleOrDescriptionContaining_NoMatch() {
        Item item = new Item(null, "Laptop", "Gaming laptop", "/img.jpg", 1000L);
        entityManager.persist(item);
        entityManager.flush();

        List<Item> found = itemRepository.findByTitleOrDescriptionContaining("phone");

        assertEquals(0, found.size());
    }

    @Test
    void testDeleteItem() {
        Item item = new Item(null, "Test Item", "Test Description", "/img.jpg", 100L);
        entityManager.persist(item);
        entityManager.flush();

        Long itemId = item.getId();
        itemRepository.deleteById(itemId);

        Optional<Item> found = itemRepository.findById(itemId);
        assertFalse(found.isPresent());
    }

    @Test
    void testUpdateItem() {
        Item item = new Item(null, "Test Item", "Test Description", "/img.jpg", 100L);
        entityManager.persist(item);
        entityManager.flush();

        item.setTitle("Updated Item");
        item.setPrice(200L);
        Item updated = itemRepository.save(item);

        assertEquals("Updated Item", updated.getTitle());
        assertEquals(200L, updated.getPrice());
    }
}

