package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllByUserId() {
    }

    @Test
    void searchAvailableItem() {
        User user = new User();
        user.setName("name");
        user.setEmail("email");
        userRepository.save(user);

        Item itemToSave = new Item(1L, "name", "testfortest", user,
                true, 1L);
        itemRepository.save(itemToSave);

        String searchText = "test";
        Pageable pageable = PageRequest.of(0, 10);

        List<Item> items = itemRepository.searchAvailableItem(searchText, pageable);

        assertNotNull(items);
        assertFalse(items.isEmpty());

        items.forEach(item -> {
            assertTrue(item.getAvailable());
            assertTrue(item.getName().toLowerCase().contains(searchText) ||
                    item.getDescription().toLowerCase().contains(searchText));
        });
        userRepository.delete(user);
        itemRepository.delete(itemToSave);
    }

    @Test
    void findAllByRequestId() {
    }

    @Test
    void findAllByRequestIdIn() {
    }
}