package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start, Pageable pageable);

    @Query("select b from Booking b " +
            "join Item i on i.id = b.item.id " +
            "where i.user.id = ?1 " +
            "order by b.start desc")
    List<Booking> findAllBookingsForUserItemsOrderByStartDesc(Long userId, Pageable pageable);

    @Query("select b from Booking b " +
            "join Item i on i.id = b.item.id " +
            "where i.user.id = ?1 " +
            "and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findAllBookingsForUserItemsByStatusOrderByStartDesc(Long userId, BookingStatus status, Pageable pageable);

    @Query("select b from Booking b " +
            "join Item i on i.id = b.item.id " +
            "where i.user.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?3 " +
            "order by b.start desc")
    List<Booking> findAllBookingsForUserItemsWhereStartBeforeAndEndAfterOrderByStartDesc(Long userId,
                                                                                         LocalDateTime start,
                                                                                         LocalDateTime end,
                                                                                         Pageable pageable);

    @Query("select b from Booking b " +
            "join Item i on i.id = b.item.id " +
            "where i.user.id = ?1 " +
            "and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findAllBookingsForUserItemsWhereEndBeforeOrderByStartDesc(Long userId, LocalDateTime start, Pageable pageable);

    @Query("select b from Booking b " +
            "join Item i on i.id = b.item.id " +
            "where i.user.id = ?1 " +
            "and b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findAllBookingsForUserItemsWhereStartAfterOrderByStartDesc(Long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItemIdOrderByEndDesc(Long itemId);

    List<Booking> findAllByItemInOrderByEndDesc(List<Item> items);
}
