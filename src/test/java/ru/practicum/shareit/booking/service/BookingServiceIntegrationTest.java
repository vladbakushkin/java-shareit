package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void getAllBookingsTest() {
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
        booking1.setEnd(LocalDateTime.now().plusDays(1));
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

        // when
        List<BookingResponseDto> bookings =
                bookingService.getAllBookings(user.getId(), BookingState.CURRENT, "bookings/owner", 0, 2);

        // then
        assertEquals(1, bookings.size());
        assertEquals(booking1.getStart(), bookings.get(0).getStart());
        assertEquals(booking1.getEnd(), bookings.get(0).getEnd());
        assertEquals(booking1.getStatus(), bookings.get(0).getStatus());
    }
}

