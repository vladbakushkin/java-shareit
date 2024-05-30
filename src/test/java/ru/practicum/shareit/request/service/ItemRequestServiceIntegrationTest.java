package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    public void getAllRequestsTest() {
        // given
        User user1 = new User();
        user1.setName("name1");
        user1.setEmail("email1@email.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("name2");
        user2.setEmail("email2@email.com");
        userRepository.save(user2);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setDescription("description1");
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setOwner(user1);
        itemRequestRepository.save(itemRequest1);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setDescription("description2");
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setOwner(user2);
        itemRequestRepository.save(itemRequest2);

        // when
        List<ItemRequestResponseDto> itemRequests = itemRequestService.getAllRequests(user1.getId(), 0, 2);

        // then
        assertEquals(1, itemRequests.size());
        assertEquals(itemRequest2.getDescription(), itemRequests.get(0).getDescription());
        assertEquals(itemRequest2.getCreated(), itemRequests.get(0).getCreated());
    }
}

