package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemListingDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    Item addedItem;
    Item updatedItem;
    User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "user@email.com", "username");
        addedItem = new Item(1L, "Item", "Description", user, true);
        updatedItem = new Item(1L, "Updated Item", "Updated Description", user, false);
    }

    @Test
    void addItem_Valid_ReturnItem() {
        // given
        ItemDto itemToAdd = createItemDto();
        when(itemRepository.save(any(Item.class))).thenReturn(addedItem);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));

        // when
        ItemDto addedItem = itemService.addItem(1L, itemToAdd);

        // then
        assertNotNull(addedItem);
        assertEquals(itemToAdd.getName(), addedItem.getName());
        assertEquals(itemToAdd.getDescription(), addedItem.getDescription());
        assertEquals(itemToAdd.getAvailable(), addedItem.getAvailable());
    }

    @Test
    void updateItem_Valid_ReturnItem() {
        // given
        UpdateItemDto itemToUpdate = createUpdateItemDto();
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(updatedItem));

        // when
        UpdateItemDto updatedItem = itemService.updateItem(1L, 1L, itemToUpdate);

        // then
        assertNotNull(updatedItem);
        assertEquals(itemToUpdate.getId(), updatedItem.getId());
        assertEquals(itemToUpdate.getName(), updatedItem.getName());
        assertEquals(itemToUpdate.getDescription(), updatedItem.getDescription());
        assertEquals(itemToUpdate.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    void getItem_Valid_ReturnItem() {
        // given
        ItemListingDto itemToGet = createItemListingDto();
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(addedItem));

        // when
        ItemListingDto addedItem = itemService.getItem(1L, 1L);

        // then
        assertNotNull(addedItem);
        assertEquals(itemToGet.getName(), addedItem.getName());
        assertEquals(itemToGet.getDescription(), addedItem.getDescription());
        assertEquals(itemToGet.getAvailable(), addedItem.getAvailable());
    }

    @Test
    void getAllItemsByOwner_Valid_ReturnItems() {
        // given
        ItemListingDto itemToGet1 = createItemListingDto();
        ItemListingDto itemToGet2 = createItemListingDto();
        when(itemRepository.findAllByUserId(any(Long.class))).thenReturn(List.of(addedItem, addedItem));

        // when
        List<ItemListingDto> items = itemService.getAllItemsByOwner(1L);

        // then
        assertNotNull(items);
        assertEquals(itemToGet1.getName(), items.get(0).getName());
        assertEquals(itemToGet1.getDescription(), items.get(0).getDescription());
        assertEquals(itemToGet1.getAvailable(), items.get(0).getAvailable());
        assertEquals(itemToGet2.getName(), items.get(1).getName());
        assertEquals(itemToGet2.getDescription(), items.get(1).getDescription());
        assertEquals(itemToGet2.getAvailable(), items.get(1).getAvailable());
    }

    @Test
    void searchAvailableItem_Valid_ReturnItems() {
        // given
        ItemDto itemToSearch = createItemDto();
        when(itemRepository.searchAvailableItem(any(String.class))).thenReturn(List.of(addedItem));

        // when
        List<ItemDto> availableItems = itemService.searchAvailableItem("sCripT");

        // then
        assertNotNull(availableItems);
        assertEquals(itemToSearch.getName(), availableItems.get(0).getName());
        assertEquals(itemToSearch.getDescription(), availableItems.get(0).getDescription());
        assertEquals(itemToSearch.getAvailable(), availableItems.get(0).getAvailable());
    }

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();
    }

    private UpdateItemDto createUpdateItemDto() {
        return UpdateItemDto.builder()
                .id(1L)
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();
    }

    private ItemListingDto createItemListingDto() {
        return ItemListingDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();
    }
}