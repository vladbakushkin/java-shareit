package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.NewUserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemServiceImpl itemService;

    Item addedItem;
    Item updatedItem;

    @BeforeEach
    void setUp() {
        addedItem = new Item(1L, "Item", "Description", 1L, true);
        updatedItem = new Item(1L, "Updated Item", "Updated Description", 1L, false);
    }

    @Test
    void addItem_Valid_ReturnItem() {
        // given
        ItemDto itemToAdd = createItemDto();
        when(itemRepository.save(any(Long.class), any(Item.class))).thenReturn(addedItem);
        when(userService.getUser(any(Long.class))).thenReturn(createNewUserDto());

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
        when(itemRepository.update(any(Long.class), any(Long.class), any(Item.class))).thenReturn(updatedItem);
        when(userService.getUser(any(Long.class))).thenReturn(createNewUserDto());
        when(itemRepository.findItemById(any(Long.class))).thenReturn(updatedItem);

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
        ItemDto itemToGet = createItemDto();
        when(itemRepository.findItemById(any(Long.class))).thenReturn(addedItem);

        // when
        ItemDto addedItem = itemService.getItem(1L);

        // then
        assertNotNull(addedItem);
        assertEquals(itemToGet.getName(), addedItem.getName());
        assertEquals(itemToGet.getDescription(), addedItem.getDescription());
        assertEquals(itemToGet.getAvailable(), addedItem.getAvailable());
    }

    @Test
    void getAllItemsByOwner_Valid_ReturnItems() {
        // given
        ItemDto itemToGet1 = createItemDto();
        UpdateItemDto itemToGet2 = createUpdateItemDto();
        when(itemRepository.findAllItemsByUserId(any(Long.class))).thenReturn(List.of(addedItem, updatedItem));

        // when
        List<ItemDto> items = itemService.getAllItemsByOwner(1L);

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

    private NewUserDto createNewUserDto() {
        NewUserDtoMapper newUserDtoMapper = new NewUserDtoMapper();
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@email.com");
        return newUserDtoMapper.toDto(user);
    }
}