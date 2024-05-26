package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utility.ItemRequestDtoMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestResponseDto addRequest(Long userId, ItemRequestRequestDto itemRequestRequestDto) {
        ItemRequest itemRequest = ItemRequestDtoMapper.toItemRequest(itemRequestRequestDto);
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));
        itemRequest.setOwner(owner);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestDtoMapper.toItemRequestResponseDto(savedItemRequest);
    }

    @Override
    public List<ItemRequestResponseDto> getMyRequests(Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByOwnerId(owner.getId());

        List<Item> items = itemRepository.findAllByRequestIdIn(itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList()));

        return itemRequests.stream()
                .map(itemRequest -> makeItemRequestWithItems(itemRequest, items))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestResponseDto> getAllRequests(Long userId, Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("'size' must be > 0 and 'from' must be >= 0. " +
                    "size = " + size + ", from = " + from);
        }

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByOwnerIdNot(pageable, userId).getContent();

        List<Item> items = itemRepository.findAllByRequestIdIn(itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList()));

        return itemRequests.stream()
                .map(itemRequest -> makeItemRequestWithItems(itemRequest, items))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponseDto getRequest(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id = " + requestId + " not found"));

        List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());

        return makeItemRequestWithItems(itemRequest, items);
    }

    private ItemRequestResponseDto makeItemRequestWithItems(ItemRequest itemRequest, List<Item> items) {
        ItemRequestResponseDto responseDto = ItemRequestDtoMapper.toItemRequestResponseDto(itemRequest);
        responseDto.setItems(items);
        return responseDto;
    }
}
