package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void addRequest_DescriptionIsValid_ReturnsRequest() {
        // given
        Long userId = 1L;
        ItemRequestRequestDto requestDto = new ItemRequestRequestDto();
        User owner = new User();
        ItemRequest itemRequest = new ItemRequest();
        ItemRequestResponseDto expectedResponseDto = new ItemRequestResponseDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        // when
        ItemRequestResponseDto actualResponseDto = itemRequestService.addRequest(userId, requestDto);

        // then
        assertEquals(expectedResponseDto, actualResponseDto);
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void getMyRequests_ArgumentsIsValid_ReturnsAllRequests() {
        // given
        int from = 0;
        int size = 10;
        Long userId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        List<ItemRequest> itemRequests = List.of(itemRequest);
        List<Item> items = List.of(new Item());
        ItemRequestResponseDto expectedResponseDto = new ItemRequestResponseDto();
        expectedResponseDto.setItems(items);

        when(itemRequestRepository.findAllByOwnerIdNot(anyLong(), any(Pageable.class))).thenReturn(itemRequests);

        when(itemRepository.findAllByRequestIdIn(any())).thenReturn(items);

        // when
        List<ItemRequestResponseDto> actualResponseDto = itemRequestService.getAllRequests(userId, from, size);

        // then
        assertEquals(List.of(expectedResponseDto), actualResponseDto);
    }

    @Test
    void getAllRequests() {
        // given
        Long userId = 1L;
        User owner = new User();
        owner.setId(userId);
        ItemRequest itemRequest = new ItemRequest();
        List<ItemRequest> itemRequests = List.of(itemRequest);
        List<Item> items = List.of(new Item());
        ItemRequestResponseDto expectedResponseDto = new ItemRequestResponseDto();
        expectedResponseDto.setItems(items);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));

        when(itemRequestRepository.findAllByOwnerId(any())).thenReturn(itemRequests);

        when(itemRepository.findAllByRequestIdIn(any())).thenReturn(items);

        // when
        List<ItemRequestResponseDto> actualResponseDto = itemRequestService.getMyRequests(userId);

        // then
        assertEquals(List.of(expectedResponseDto), actualResponseDto);
    }

    @Test
    void getRequest_ItemRequestFound_ReturnItemRequest() {
        // given
        long itemRequestId = 1L;
        long userId = 1L;
        User user = new User();
        ItemRequest expectedItemRequest = new ItemRequest();
        ArrayList<Item> items = new ArrayList<>();

        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.of(expectedItemRequest));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findAllByRequestId(any())).thenReturn(items);

        // when
        ItemRequestResponseDto actualItemRequest = itemRequestService.getRequest(userId, itemRequestId);

        // then
        assertEquals(expectedItemRequest.getCreated(), actualItemRequest.getCreated());
        assertEquals(expectedItemRequest.getDescription(), actualItemRequest.getDescription());
        assertEquals(items, actualItemRequest.getItems());
    }

    @Test
    void getRequest_ItemRequestNotFound_ThrowsException() {
        // given
        long itemRequestId = 1L;
        long userId = 1L;
        User user = new User();

        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // then
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequest(userId, itemRequestId));
    }
}