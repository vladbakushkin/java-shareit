package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utility.BookingDtoMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    Item addedItem;
    Item updatedItem;
    User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "user@email.com", "username");
        addedItem = new Item(1L, "Item", "Description", user, true, 1L);
        updatedItem = new Item(1L, "Updated Item", "Updated Description", user, false,
                1L);
    }

    @Test
    void addItem_Valid_ReturnItem() {
        // given
        ItemRequestDto itemToAdd = createItemDto();
        when(itemRepository.save(any(Item.class))).thenReturn(addedItem);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));

        // when
        ItemDetailsDto addedItem = itemService.addItem(1L, itemToAdd);

        // then
        assertNotNull(addedItem);
        assertEquals(itemToAdd.getName(), addedItem.getName());
        assertEquals(itemToAdd.getDescription(), addedItem.getDescription());
        assertEquals(itemToAdd.getAvailable(), addedItem.getAvailable());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void addItem_WithRequestIdValid_ReturnItem() {
        // given
        ItemRequestDto itemToAdd = createItemDto();
        itemToAdd.setRequestId(1L);
        when(itemRepository.save(any(Item.class))).thenReturn(addedItem);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        when(itemRequestRepository.findById(itemToAdd.getRequestId())).thenReturn(Optional.of(itemRequest));

        // when
        ItemDetailsDto addedItem = itemService.addItem(1L, itemToAdd);

        // then
        assertNotNull(addedItem);
        assertEquals(itemToAdd.getName(), addedItem.getName());
        assertEquals(itemToAdd.getDescription(), addedItem.getDescription());
        assertEquals(itemToAdd.getAvailable(), addedItem.getAvailable());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_Valid_ReturnItem() {
        // given
        ItemUpdateDto itemToUpdate = createUpdateItemDto();
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(updatedItem));

        // when
        ItemDetailsDto updatedItem = itemService.updateItem(1L, 1L, itemToUpdate);

        // then
        assertNotNull(updatedItem);
        assertEquals(itemToUpdate.getId(), updatedItem.getId());
        assertEquals(itemToUpdate.getName(), updatedItem.getName());
        assertEquals(itemToUpdate.getDescription(), updatedItem.getDescription());
        assertEquals(itemToUpdate.getAvailable(), updatedItem.getAvailable());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_WithoutFieldsValid_ReturnItem() {
        // given
        Item item = new Item();
        ItemUpdateDto itemToUpdate = createUpdateItemDto();
        itemToUpdate.setName(null);
        itemToUpdate.setDescription(null);
        itemToUpdate.setAvailable(null);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(updatedItem));

        // when
        ItemDetailsDto updatedItem = itemService.updateItem(1L, 1L, itemToUpdate);

        // then
        assertNotNull(updatedItem);
        assertEquals(itemToUpdate.getName(), updatedItem.getName());
        assertEquals(itemToUpdate.getDescription(), updatedItem.getDescription());
        assertEquals(itemToUpdate.getAvailable(), updatedItem.getAvailable());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_NotOwnerInvalid_ThrowsNotFoundException() {
        // given
        ItemUpdateDto itemToUpdate = createUpdateItemDto();
        updatedItem.setUser(new User(2L, "user@email.com", "username"));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(updatedItem));

        // then
        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, 1L, itemToUpdate));
    }

    @Test
    void getItem_Valid_ReturnItem() {
        // given
        ItemDetailsDto itemToGet = createItemDetailsDto();
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(addedItem));

        // when
        ItemDetailsDto addedItem = itemService.getItem(1L, 1L);

        // then
        assertNotNull(addedItem);
        assertEquals(itemToGet.getName(), addedItem.getName());
        assertEquals(itemToGet.getDescription(), addedItem.getDescription());
        assertEquals(itemToGet.getAvailable(), addedItem.getAvailable());
    }

    @Test
    void getItem_WithLastBookingValid_ReturnItem() {
        // given
        ItemDetailsDto itemToGet = createItemDetailsDto();
        Booking booking = new Booking(1L, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1),
                updatedItem, user, BookingStatus.APPROVED);
        BookingRequestDto bookingRequestDto = BookingDtoMapper.toBookingRequestDto(booking);

        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(addedItem));
        when(bookingRepository.findAllByItemIdOrderByEndDesc(any(Long.class))).thenReturn(List.of(booking));

        // when
        ItemDetailsDto addedItem = itemService.getItem(1L, 1L);

        // then
        assertNotNull(addedItem);
        assertEquals(itemToGet.getName(), addedItem.getName());
        assertEquals(itemToGet.getDescription(), addedItem.getDescription());
        assertEquals(itemToGet.getAvailable(), addedItem.getAvailable());
        assertEquals(bookingRequestDto, addedItem.getLastBooking());
    }

    @Test
    void getItem_WithNextBookingValid_ReturnItem() {
        // given
        ItemDetailsDto itemToGet = createItemDetailsDto();
        Booking booking = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2),
                updatedItem, user, BookingStatus.APPROVED);
        BookingRequestDto bookingRequestDto = BookingDtoMapper.toBookingRequestDto(booking);

        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(addedItem));
        when(bookingRepository.findAllByItemIdOrderByEndDesc(any(Long.class))).thenReturn(List.of(booking));

        // when
        ItemDetailsDto addedItem = itemService.getItem(1L, 1L);

        // then
        assertNotNull(addedItem);
        assertEquals(itemToGet.getName(), addedItem.getName());
        assertEquals(itemToGet.getDescription(), addedItem.getDescription());
        assertEquals(itemToGet.getAvailable(), addedItem.getAvailable());
        assertEquals(bookingRequestDto, addedItem.getNextBooking());
    }

    @Test
    void getItem_WithoutCommentsValid_ReturnItem() {
        // given
        ItemDetailsDto itemToGet = createItemDetailsDto();

        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(addedItem));
        when(commentRepository.findAllByItemId(any(Long.class))).thenReturn(null);

        // when
        ItemDetailsDto addedItem = itemService.getItem(1L, 1L);

        // then
        assertNotNull(addedItem);
        assertEquals(itemToGet.getName(), addedItem.getName());
        assertEquals(itemToGet.getDescription(), addedItem.getDescription());
        assertEquals(itemToGet.getAvailable(), addedItem.getAvailable());
        assertEquals(itemToGet.getComments(), addedItem.getComments());
    }

    @Test
    void getAllItemsByOwner_Valid_ReturnItems() {
        // given
        ItemDetailsDto itemToGet1 = createItemDetailsDto();
        ItemDetailsDto itemToGet2 = createItemDetailsDto();
        when(itemRepository.findAllByUserId(any(Long.class), any(Pageable.class)))
                .thenReturn(List.of(addedItem, addedItem));

        // when
        List<ItemDetailsDto> items = itemService.getAllItemsByOwner(1L, 0, 10);

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
    void getAllItemsByOwner_ArgumentFromIsInvalid_ThrowsBadRequestException() {
        // given
        int from = -1;
        int size = 10;
        long userId = 1;

        // then
        assertThrows(BadRequestException.class,
                () -> itemService.getAllItemsByOwner(userId, from, size));
    }

    @Test
    void getAllItemsByOwner_ArgumentSizeIsInvalid_ThrowsBadRequestException() {
        // given
        int from = 0;
        int size = 0;
        long userId = 1;

        // then
        assertThrows(BadRequestException.class,
                () -> itemService.getAllItemsByOwner(userId, from, size));
    }

    @Test
    void searchAvailableItem_Valid_ReturnItems() {
        // given
        ItemRequestDto itemToSearch = createItemDto();
        when(itemRepository.searchAvailableItem(any(String.class), any(Pageable.class)))
                .thenReturn(List.of(addedItem));

        // when
        List<ItemDetailsDto> availableItems = itemService.searchAvailableItem("sCripT", 0, 10);

        // then
        assertNotNull(availableItems);
        assertEquals(itemToSearch.getName(), availableItems.get(0).getName());
        assertEquals(itemToSearch.getDescription(), availableItems.get(0).getDescription());
        assertEquals(itemToSearch.getAvailable(), availableItems.get(0).getAvailable());
    }

    @Test
    void searchAvailableItem_WithBlankRequestValid_ReturnItems() {
        // when
        List<ItemDetailsDto> availableItems = itemService.searchAvailableItem("", 0, 10);

        // then
        assertNotNull(availableItems);
        assertEquals(0, availableItems.size());
    }

    @Test
    void searchAvailableItem_ArgumentFromIsInvalid_ThrowsBadRequestException() {
        // given
        int from = -1;
        int size = 10;

        // then
        assertThrows(BadRequestException.class,
                () -> itemService.searchAvailableItem("item", from, size));
    }

    @Test
    void searchAvailableItem_ArgumentSizeIsInvalid_ThrowsBadRequestException() {
        // given
        int from = 0;
        int size = 0;

        // then
        assertThrows(BadRequestException.class,
                () -> itemService.searchAvailableItem("item", from, size));
    }

    @Test
    void addComment_Valid_ReturnComment() {
        // given
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(updatedItem));
        Booking booking = new Booking(1L, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1),
                updatedItem, user, BookingStatus.APPROVED);
        when(bookingRepository.findAllByItemIdOrderByEndDesc(any(Long.class))).thenReturn(List.of(booking));
        CommentRequestDto commentRequestDto = new CommentRequestDto(1L, "text", updatedItem, user);
        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setItem(updatedItem);
        comment.setText("text");
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // when
        CommentResponseDto commentResponseDto = itemService.addComment(1L, 1L, commentRequestDto);

        // then
        assertEquals(commentRequestDto.getText(), commentResponseDto.getText());
        assertEquals(commentRequestDto.getAuthor().getName(), commentResponseDto.getAuthorName());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_UserNotBookerInvalid_ThrowsBadRequestException() {
        // given
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(updatedItem));
        Booking booking = new Booking(1L, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1),
                updatedItem, new User(), BookingStatus.APPROVED);
        when(bookingRepository.findAllByItemIdOrderByEndDesc(any(Long.class))).thenReturn(List.of(booking));
        CommentRequestDto commentRequestDto = new CommentRequestDto(1L, "text", updatedItem, user);

        // then
        assertThrows(BadRequestException.class,
                () -> itemService.addComment(1L, 1L, commentRequestDto));
    }

    @Test
    void addComment_UserNotBookingTimeInvalid_ThrowsBadRequestException() {
        // given
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(updatedItem));
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                updatedItem, user, BookingStatus.APPROVED);
        when(bookingRepository.findAllByItemIdOrderByEndDesc(any(Long.class))).thenReturn(List.of(booking));
        CommentRequestDto commentRequestDto = new CommentRequestDto(1L, "text", updatedItem, user);

        // then
        assertThrows(BadRequestException.class,
                () -> itemService.addComment(1L, 1L, commentRequestDto));
    }

    private ItemRequestDto createItemDto() {
        return ItemRequestDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();
    }

    private ItemUpdateDto createUpdateItemDto() {
        return ItemUpdateDto.builder()
                .id(1L)
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();
    }

    private ItemDetailsDto createItemDetailsDto() {
        return ItemDetailsDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();
    }
}