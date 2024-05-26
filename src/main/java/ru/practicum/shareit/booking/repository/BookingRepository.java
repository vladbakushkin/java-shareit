package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start, LocalDateTime end,
                                                             Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime start, Pageable pageable);


    List<Booking> findAllBookingsByItem_UserId(Long userId, Pageable pageable);

    List<Booking> findAllBookingsByItem_UserIdAndStatus(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findAllBookingsByItem_UserIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime start,
                                                                        LocalDateTime end, Pageable pageable);

    List<Booking> findAllBookingsByItem_UserIdAndEndBefore(Long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllBookingsByItem_UserIdAndStartAfter(Long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItemIdOrderByEndDesc(Long itemId);

    List<Booking> findAllByItemInOrderByEndDesc(List<Item> items);
}
