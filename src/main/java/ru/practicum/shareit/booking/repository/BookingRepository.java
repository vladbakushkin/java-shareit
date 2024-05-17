package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    @Query("select b from Booking b " +
            "join Item i on i.id = b.item.id " +
            "where i.user.id = ?1 " +
            "order by b.start desc")
    List<Booking> findAllBookingsForUserItemsStateAllOrderByStartDesc(Long userId);

    @Query("select b from Booking b " +
            "join Item i on i.id = b.item.id " +
            "where i.user.id = ?1 " +
            "and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findAllBookingsForUserItemsByStatusOrderByStartDesc(Long userId, BookingStatus status);

    @Query("select b from Booking b " +
            "join Item i on i.id = b.item.id " +
            "where i.user.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?3 " +
            "order by b.start desc")
    List<Booking> findAllBookingsForUserItemsStateCurrentOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end);

    @Query("select b from Booking b " +
            "join Item i on i.id = b.item.id " +
            "where i.user.id = ?1 " +
            "and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findAllBookingsForUserItemsStatePastOrderByStartDesc(Long userId, LocalDateTime start);

    @Query("select b from Booking b " +
            "join Item i on i.id = b.item.id " +
            "where i.user.id = ?1 " +
            "and b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findAllBookingsForUserItemsStateFutureOrderByStartDesc(Long userId, LocalDateTime start);

    List<Booking> findAllByItemIdOrderByEndDesc(Long itemId);

    List<Booking> findAllByItemIdOrderByStartAsc(Long itemId);
}
