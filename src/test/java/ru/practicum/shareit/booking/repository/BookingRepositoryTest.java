package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void findAllByBookerId() {
    }

    @Test
    void findAllByBookerIdAndStatus() {
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfter() {
    }

    @Test
    void findAllByBookerIdAndEndBefore() {
    }

    @Test
    void findAllByBookerIdAndStartAfter() {
    }

    @Test
    void findAllBookingsByItem_UserId() {
    }

    @Test
    void findAllBookingsByItem_UserIdAndStatus() {
    }

    @Test
    void findAllBookingsByItem_UserIdAndStartBeforeAndEndAfter() {
    }

    @Test
    void findAllBookingsByItem_UserIdAndEndBefore() {
    }

    @Test
    void findAllBookingsByItem_UserIdAndStartAfter() {
    }

    @Test
    void findAllByItemIdOrderByEndDesc() {
    }

    @Test
    void findAllByItemInOrderByEndDesc() {
    }
}