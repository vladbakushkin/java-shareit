package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void getAllItemsByOwnerTest() {
        // given
        User user = new User();
        user.setName("name");
        user.setEmail("email@email.com");
        userRepository.save(user);

        Item item1 = new Item();
        item1.setName("item1");
        item1.setDescription("description1");
        item1.setAvailable(true);
        item1.setUser(user);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("item2");
        item2.setDescription("description2");
        item2.setAvailable(false);
        item2.setUser(user);
        itemRepository.save(item2);

        Booking booking1 = new Booking();
        booking1.setStart(LocalDateTime.now().minusDays(2));
        booking1.setEnd(LocalDateTime.now().minusDays(1));
        booking1.setItem(item1);
        booking1.setStatus(BookingStatus.APPROVED);
        booking1.setBooker(user);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(3));
        booking2.setItem(item2);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setBooker(user);
        bookingRepository.save(booking2);

        Comment comment1 = new Comment();
        comment1.setText("text1");
        comment1.setAuthor(user);
        comment1.setItem(item1);
        commentRepository.save(comment1);

        Comment comment2 = new Comment();
        comment2.setText("text2");
        comment2.setAuthor(user);
        comment2.setItem(item2);
        commentRepository.save(comment2);

        // when
        List<ItemResponseDto> items = itemService.getAllItemsByOwner(user.getId(), 0, 2);

        // then
        assertThat(items.size(), equalTo(2));
        assertThat(items.get(0).getLastBooking(), notNullValue());
        assertThat(items.get(1).getNextBooking(), notNullValue());
        assertThat(items.get(0).getComments().size(), equalTo(2));
        assertThat(items.get(1).getComments().size(), equalTo(2));
    }
}

