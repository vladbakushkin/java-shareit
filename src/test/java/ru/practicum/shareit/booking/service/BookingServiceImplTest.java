package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void addBooking_isValid_ReturnsBookingResponseDto() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingRequestDto requestDto = new BookingRequestDto(1L, start, end, 1L, 1L,
                BookingStatus.WAITING);

        Item item = new Item();
        item.setUser(new User(2L, "email", "name"));
        item.setAvailable(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // when
        BookingResponseDto bookingResponseDto = bookingService.addBooking(1L, requestDto);

        // then
        assertEquals(requestDto.getStart(), bookingResponseDto.getStart());
        assertEquals(requestDto.getEnd(), bookingResponseDto.getEnd());
        assertEquals(BookingStatus.WAITING, bookingResponseDto.getStatus());
        assertEquals(item, bookingResponseDto.getItem());
        assertEquals(user, bookingResponseDto.getBooker());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void addBooking_StartTimeEqualEndTime_ThrowsBadRequestException() {
        // given
        LocalDateTime start = LocalDateTime.now();
        BookingRequestDto requestDto = new BookingRequestDto(1L, start, start, 1L, 1L,
                BookingStatus.WAITING);

        // then
        assertThrows(BadRequestException.class,
                () -> bookingService.addBooking(1L, requestDto));
    }

    @Test
    void addBooking_EndTimeBeforeStartTime_ThrowsBadRequestException() {
        // given
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingRequestDto requestDto = new BookingRequestDto(1L, start, end, 1L, 1L,
                BookingStatus.WAITING);

        // then
        assertThrows(BadRequestException.class,
                () -> bookingService.addBooking(1L, requestDto));
    }

    @Test
    void addBooking_BookingYourself_ThrowsNotFoundException() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingRequestDto requestDto = new BookingRequestDto(1L, start, end, 1L, 1L,
                BookingStatus.WAITING);

        Item item = new Item();
        item.setUser(new User(1L, "email", "name"));
        item.setAvailable(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // then
        assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(1L, requestDto));
    }

    @Test
    void addBooking_BookingYourself_ThrowsBadRequestException() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        BookingRequestDto requestDto = new BookingRequestDto(1L, start, end, 1L, 1L,
                BookingStatus.WAITING);

        Item item = new Item();
        item.setUser(new User(2L, "email", "name"));
        item.setAvailable(false);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // then
        assertThrows(BadRequestException.class,
                () -> bookingService.addBooking(1L, requestDto));
    }

    @Test
    void handleBooking_approvedTrue_ReturnsBookingResponseDto() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Item item = new Item();
        item.setUser(new User(1L, "email", "name"));
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // when
        BookingResponseDto bookingResponseDto = bookingService.handleBooking(user.getId(), booking.getId(),
                true);

        // then
        assertEquals(BookingStatus.APPROVED, bookingResponseDto.getStatus());
        assertEquals(start, bookingResponseDto.getStart());
        assertEquals(end, bookingResponseDto.getEnd());
        assertEquals(item, bookingResponseDto.getItem());
        assertEquals(user, bookingResponseDto.getBooker());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void handleBooking_approvedFalse_ReturnsBookingResponseDto() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Item item = new Item();
        item.setUser(new User(1L, "email", "name"));
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // when
        BookingResponseDto bookingResponseDto = bookingService.handleBooking(user.getId(), booking.getId(),
                false);

        // then
        assertEquals(BookingStatus.REJECTED, bookingResponseDto.getStatus());
        assertEquals(start, bookingResponseDto.getStart());
        assertEquals(end, bookingResponseDto.getEnd());
        assertEquals(item, bookingResponseDto.getItem());
        assertEquals(user, bookingResponseDto.getBooker());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void handleBooking_userNotOwner_ThrowsNotFoundException() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Item item = new Item();
        item.setUser(new User(2L, "email", "name"));
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        // then
        assertThrows(NotFoundException.class,
                () -> bookingService.handleBooking(user.getId(), booking.getId(), true));
    }

    @Test
    void handleBooking_bookingAlreadyApproved_ThrowsBadRequestException() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Item item = new Item();
        item.setUser(new User(1L, "email", "name"));
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        // then
        assertThrows(BadRequestException.class,
                () -> bookingService.handleBooking(user.getId(), booking.getId(), true));
    }

    @Test
    void getBooking_isValid_ReturnsBookingResponseDto() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Item item = new Item();
        item.setUser(new User(2L, "email", "name"));
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        // when
        BookingResponseDto actualBooking = bookingService.getBooking(1L, 1L);

        // then
        assertEquals(booking.getId(), actualBooking.getId());
        assertEquals(booking.getBooker(), actualBooking.getBooker());
        assertEquals(booking.getStart(), actualBooking.getStart());
        assertEquals(booking.getEnd(), actualBooking.getEnd());
        assertEquals(booking.getItem(), actualBooking.getItem());
        assertEquals(booking.getStatus(), actualBooking.getStatus());
    }

    @Test
    void getBooking_userNotBooker_ThrowsNotFoundException() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Item item = new Item();
        item.setUser(new User(2L, "email", "name"));
        item.setAvailable(true);

        User user = new User();
        user.setId(2L);

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        // then
        assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(1L, 1L));
    }

    @Test
    void getAllBookings_pathOwnerCaseAllIsValid_ReturnsListOfBookingResponseDto() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Item item = new Item();
        item.setUser(new User(2L, "email", "name"));
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);

        when(bookingRepository.findAllBookingsByItem_UserId(anyLong(), any())).thenReturn(List.of(booking));

        // when
        List<BookingResponseDto> allBookings =
                bookingService.getAllBookings(1L, BookingState.ALL, "bookings/owner", 0, 10);

        // then
        assertEquals(booking.getId(), allBookings.get(0).getId());
        assertEquals(booking.getBooker(), allBookings.get(0).getBooker());
        assertEquals(booking.getStart(), allBookings.get(0).getStart());
        assertEquals(booking.getEnd(), allBookings.get(0).getEnd());
        assertEquals(booking.getItem(), allBookings.get(0).getItem());
        assertEquals(booking.getStatus(), allBookings.get(0).getStatus());
    }

    @Test
    void getAllBookings_pathOwnerCaseCurrentIsValid_ReturnsListOfBookingResponseDto() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Item item = new Item();
        item.setUser(new User(2L, "email", "name"));
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);

        when(bookingRepository.findAllBookingsByItem_UserIdAndStartBeforeAndEndAfter(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        // when
        List<BookingResponseDto> allBookings =
                bookingService.getAllBookings(1L, BookingState.CURRENT, "bookings/owner", 0, 10);

        // then
        assertEquals(booking.getId(), allBookings.get(0).getId());
        assertEquals(booking.getBooker(), allBookings.get(0).getBooker());
        assertEquals(booking.getStart(), allBookings.get(0).getStart());
        assertEquals(booking.getEnd(), allBookings.get(0).getEnd());
        assertEquals(booking.getItem(), allBookings.get(0).getItem());
        assertEquals(booking.getStatus(), allBookings.get(0).getStatus());
    }

    @Test
    void getAllBookings_pathOwnerCasePastIsValid_ReturnsListOfBookingResponseDto() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Item item = new Item();
        item.setUser(new User(2L, "email", "name"));
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);

        when(bookingRepository.findAllBookingsByItem_UserIdAndEndBefore(anyLong(),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        // when
        List<BookingResponseDto> allBookings =
                bookingService.getAllBookings(1L, BookingState.PAST, "bookings/owner", 0, 10);

        // then
        assertEquals(booking.getId(), allBookings.get(0).getId());
        assertEquals(booking.getBooker(), allBookings.get(0).getBooker());
        assertEquals(booking.getStart(), allBookings.get(0).getStart());
        assertEquals(booking.getEnd(), allBookings.get(0).getEnd());
        assertEquals(booking.getItem(), allBookings.get(0).getItem());
        assertEquals(booking.getStatus(), allBookings.get(0).getStatus());
    }

    @Test
    void getAllBookings_pathOwnerCaseFutureIsValid_ReturnsListOfBookingResponseDto() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Item item = new Item();
        item.setUser(new User(2L, "email", "name"));
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);

        when(bookingRepository.findAllBookingsByItem_UserIdAndStartAfter(anyLong(),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        // when
        List<BookingResponseDto> allBookings =
                bookingService.getAllBookings(1L, BookingState.FUTURE, "bookings/owner", 0, 10);

        // then
        assertEquals(booking.getId(), allBookings.get(0).getId());
        assertEquals(booking.getBooker(), allBookings.get(0).getBooker());
        assertEquals(booking.getStart(), allBookings.get(0).getStart());
        assertEquals(booking.getEnd(), allBookings.get(0).getEnd());
        assertEquals(booking.getItem(), allBookings.get(0).getItem());
        assertEquals(booking.getStatus(), allBookings.get(0).getStatus());
    }

    @Test
    void getAllBookings_pathOwnerCaseWaitingIsValid_ReturnsListOfBookingResponseDto() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Item item = new Item();
        item.setUser(new User(2L, "email", "name"));
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);

        when(bookingRepository.findAllBookingsByItem_UserIdAndStatus(anyLong(),
                any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        // when
        List<BookingResponseDto> allBookings =
                bookingService.getAllBookings(1L, BookingState.WAITING, "bookings/owner", 0, 10);

        // then
        assertEquals(booking.getId(), allBookings.get(0).getId());
        assertEquals(booking.getBooker(), allBookings.get(0).getBooker());
        assertEquals(booking.getStart(), allBookings.get(0).getStart());
        assertEquals(booking.getEnd(), allBookings.get(0).getEnd());
        assertEquals(booking.getItem(), allBookings.get(0).getItem());
        assertEquals(booking.getStatus(), allBookings.get(0).getStatus());
    }

    @Test
    void getAllBookings_pathOwnerCaseRejectedIsValid_ReturnsListOfBookingResponseDto() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Item item = new Item();
        item.setUser(new User(2L, "email", "name"));
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);

        when(bookingRepository.findAllBookingsByItem_UserIdAndStatus(anyLong(),
                any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        // when
        List<BookingResponseDto> allBookings =
                bookingService.getAllBookings(1L, BookingState.REJECTED, "bookings/owner", 0, 10);

        // then
        assertEquals(booking.getId(), allBookings.get(0).getId());
        assertEquals(booking.getBooker(), allBookings.get(0).getBooker());
        assertEquals(booking.getStart(), allBookings.get(0).getStart());
        assertEquals(booking.getEnd(), allBookings.get(0).getEnd());
        assertEquals(booking.getItem(), allBookings.get(0).getItem());
        assertEquals(booking.getStatus(), allBookings.get(0).getStatus());
    }

    @Test
    void getAllBookings_caseAllIsValid_ReturnsListOfBookingResponseDto() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Item item = new Item();
        item.setUser(new User(2L, "email", "name"));
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);

        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(List.of(booking));

        // when
        List<BookingResponseDto> allBookings =
                bookingService.getAllBookings(1L, BookingState.ALL, "bookings", 0, 10);

        // then
        assertEquals(booking.getId(), allBookings.get(0).getId());
        assertEquals(booking.getBooker(), allBookings.get(0).getBooker());
        assertEquals(booking.getStart(), allBookings.get(0).getStart());
        assertEquals(booking.getEnd(), allBookings.get(0).getEnd());
        assertEquals(booking.getItem(), allBookings.get(0).getItem());
        assertEquals(booking.getStatus(), allBookings.get(0).getStatus());
    }

    @Test
    void getAllBookings_caseCurrentIsValid_ReturnsListOfBookingResponseDto() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Item item = new Item();
        item.setUser(new User(2L, "email", "name"));
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);

        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        // when
        List<BookingResponseDto> allBookings =
                bookingService.getAllBookings(1L, BookingState.CURRENT, "bookings", 0, 10);

        // then
        assertEquals(booking.getId(), allBookings.get(0).getId());
        assertEquals(booking.getBooker(), allBookings.get(0).getBooker());
        assertEquals(booking.getStart(), allBookings.get(0).getStart());
        assertEquals(booking.getEnd(), allBookings.get(0).getEnd());
        assertEquals(booking.getItem(), allBookings.get(0).getItem());
        assertEquals(booking.getStatus(), allBookings.get(0).getStatus());
    }

    @Test
    void getAllBookings_casePastIsValid_ReturnsListOfBookingResponseDto() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Item item = new Item();
        item.setUser(new User(2L, "email", "name"));
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);

        when(bookingRepository.findAllByBookerIdAndEndBefore(anyLong(),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        // when
        List<BookingResponseDto> allBookings =
                bookingService.getAllBookings(1L, BookingState.PAST, "bookings", 0, 10);

        // then
        assertEquals(booking.getId(), allBookings.get(0).getId());
        assertEquals(booking.getBooker(), allBookings.get(0).getBooker());
        assertEquals(booking.getStart(), allBookings.get(0).getStart());
        assertEquals(booking.getEnd(), allBookings.get(0).getEnd());
        assertEquals(booking.getItem(), allBookings.get(0).getItem());
        assertEquals(booking.getStatus(), allBookings.get(0).getStatus());
    }

    @Test
    void getAllBookings_caseFutureIsValid_ReturnsListOfBookingResponseDto() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Item item = new Item();
        item.setUser(new User(2L, "email", "name"));
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);

        when(bookingRepository.findAllByBookerIdAndStartAfter(anyLong(),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        // when
        List<BookingResponseDto> allBookings =
                bookingService.getAllBookings(1L, BookingState.FUTURE, "bookings", 0, 10);

        // then
        assertEquals(booking.getId(), allBookings.get(0).getId());
        assertEquals(booking.getBooker(), allBookings.get(0).getBooker());
        assertEquals(booking.getStart(), allBookings.get(0).getStart());
        assertEquals(booking.getEnd(), allBookings.get(0).getEnd());
        assertEquals(booking.getItem(), allBookings.get(0).getItem());
        assertEquals(booking.getStatus(), allBookings.get(0).getStatus());
    }

    @Test
    void getAllBookings_caseWaitingIsValid_ReturnsListOfBookingResponseDto() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Item item = new Item();
        item.setUser(new User(2L, "email", "name"));
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);

        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(),
                any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        // when
        List<BookingResponseDto> allBookings =
                bookingService.getAllBookings(1L, BookingState.WAITING, "bookings", 0, 10);

        // then
        assertEquals(booking.getId(), allBookings.get(0).getId());
        assertEquals(booking.getBooker(), allBookings.get(0).getBooker());
        assertEquals(booking.getStart(), allBookings.get(0).getStart());
        assertEquals(booking.getEnd(), allBookings.get(0).getEnd());
        assertEquals(booking.getItem(), allBookings.get(0).getItem());
        assertEquals(booking.getStatus(), allBookings.get(0).getStatus());
    }

    @Test
    void getAllBookings_caseRejectedIsValid_ReturnsListOfBookingResponseDto() {
        // given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        Item item = new Item();
        item.setUser(new User(2L, "email", "name"));
        item.setAvailable(true);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);

        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        // when
        List<BookingResponseDto> allBookings =
                bookingService.getAllBookings(1L, BookingState.REJECTED, "bookings", 0, 10);

        // then
        assertEquals(booking.getId(), allBookings.get(0).getId());
        assertEquals(booking.getBooker(), allBookings.get(0).getBooker());
        assertEquals(booking.getStart(), allBookings.get(0).getStart());
        assertEquals(booking.getEnd(), allBookings.get(0).getEnd());
        assertEquals(booking.getItem(), allBookings.get(0).getItem());
        assertEquals(booking.getStatus(), allBookings.get(0).getStatus());
    }

    @Test
    void getAllBookings_fromLessZero_ThrowsBadRequestException() {
        // given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        // then
        assertThrows(BadRequestException.class,
                () -> bookingService.getAllBookings(1L, BookingState.WAITING, "path", -1, 10));
    }

    @Test
    void getAllBookings_sizeLessZero_ThrowsBadRequestException() {
        // given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        // then
        assertThrows(BadRequestException.class,
                () -> bookingService.getAllBookings(1L, BookingState.WAITING, "path", 0, -1));
    }
}