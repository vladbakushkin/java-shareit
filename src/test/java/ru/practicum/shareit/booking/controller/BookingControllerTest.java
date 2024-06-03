package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().plusHours(1);
        end = start.plusDays(1);
    }

    @SneakyThrows
    @Test
    void addBooking_isValid_ReturnsBookingResponseDto() {
        // given
        BookingRequestDto bookingRequestDto = getBookingRequestDto();
        BookingResponseDto bookingResponseDto = getBookingResponseDto();

        // when
        when(bookingService.addBooking(1L, bookingRequestDto)).thenReturn(bookingResponseDto);

        // then
        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.status").value(bookingResponseDto.getStatus().toString()))
                .andExpect(jsonPath("$.item").value(bookingResponseDto.getItem()))
                .andExpect(jsonPath("$.booker").value(bookingResponseDto.getBooker()));
    }

    @SneakyThrows
    @Test
    void addBooking_startIsPast_ThrowsBadRequestException() {
        // given
        BookingRequestDto bookingRequestDto = getBookingRequestDto();
        bookingRequestDto.setStart(LocalDateTime.now().minusHours(1));

        // then
        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addBooking_startIsNull_ThrowsBadRequestException() {
        // given
        BookingRequestDto bookingRequestDto = getBookingRequestDto();
        bookingRequestDto.setStart(null);

        // then
        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addBooking_endIsPast_ThrowsBadRequestException() {
        // given
        BookingRequestDto bookingRequestDto = getBookingRequestDto();
        bookingRequestDto.setEnd(LocalDateTime.now().minusHours(1));

        // then
        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addBooking_endIsPresent_ThrowsBadRequestException() {
        // given
        BookingRequestDto bookingRequestDto = getBookingRequestDto();
        bookingRequestDto.setEnd(LocalDateTime.now().minusHours(1));

        // then
        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addBooking_endIsNull_ThrowsBadRequestException() {
        // given
        BookingRequestDto bookingRequestDto = getBookingRequestDto();
        bookingRequestDto.setEnd(null);

        // then
        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addBooking_itemIdIsNull_ThrowsBadRequestException() {
        // given
        BookingRequestDto bookingRequestDto = getBookingRequestDto();
        bookingRequestDto.setItemId(null);

        // then
        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void handleBooking_isValid_ReturnsBookingResponseDto() {
        //given
        BookingResponseDto bookingResponseDto = getBookingResponseDto();

        long bookingId = 1L;

        // when
        when(bookingService.handleBooking(any(Long.class), any(Long.class), any(Boolean.class)))
                .thenReturn(bookingResponseDto);

        // then
        mockMvc.perform(
                        patch("/bookings/{bookingId}", bookingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.status").value(bookingResponseDto.getStatus().toString()))
                .andExpect(jsonPath("$.item").value(bookingResponseDto.getItem()))
                .andExpect(jsonPath("$.booker").value(bookingResponseDto.getBooker()));
    }

    @SneakyThrows
    @Test
    void getBooking_isValid_ReturnsBookingResponseDto() {
        //given
        BookingResponseDto bookingResponseDto = getBookingResponseDto();

        long bookingId = 1L;

        // when
        when(bookingService.getBooking(any(Long.class), any(Long.class))).thenReturn(bookingResponseDto);

        // then
        mockMvc.perform(
                        get("/bookings/{bookingId}", bookingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.status").value(bookingResponseDto.getStatus().toString()))
                .andExpect(jsonPath("$.item").value(bookingResponseDto.getItem()))
                .andExpect(jsonPath("$.booker").value(bookingResponseDto.getBooker()));
    }

    @SneakyThrows
    @Test
    void getBooking_wrongBookingId_ThrowsNotFoundException() {
        // given
        long bookingId = 99L;

        when(bookingService.getBooking(any(Long.class), any(Long.class))).thenThrow(NotFoundException.class);

        // then
        mockMvc.perform(
                        get("/bookings/{bookingId}", bookingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getAllBookingsForUser_isValid_ReturnsListOfBookingResponseDto() {
        //given
        BookingResponseDto bookingResponseDto = getBookingResponseDto();
        List<BookingResponseDto> bookings = List.of(bookingResponseDto);

        // when
        when(bookingService.getAllBookings(any(Long.class), any(BookingState.class), anyString(), anyInt(), anyInt()))
                .thenReturn(bookings);

        // then
        mockMvc.perform(
                        get("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].status").value(bookingResponseDto.getStatus().toString()))
                .andExpect(jsonPath("$[0].item").value(bookingResponseDto.getItem()))
                .andExpect(jsonPath("$[0].booker").value(bookingResponseDto.getBooker()));
    }

    @SneakyThrows
    @Test
    void getAllBookingsForUser_unknownBookingState_ThrowsUnknownBookingStateException() {
        // then
        mockMvc.perform(
                        get("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .param("state", "unknown"))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllBookingsForUserItems_isValid_ReturnsListOfBookingResponseDto() {
        //given
        BookingResponseDto bookingResponseDto = getBookingResponseDto();
        List<BookingResponseDto> bookings = List.of(bookingResponseDto);

        // when
        when(bookingService.getAllBookings(any(Long.class), any(BookingState.class), anyString(), anyInt(), anyInt()))
                .thenReturn(bookings);

        // then
        mockMvc.perform(
                        get("/bookings/owner")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].status").value(bookingResponseDto.getStatus().toString()))
                .andExpect(jsonPath("$[0].item").value(bookingResponseDto.getItem()))
                .andExpect(jsonPath("$[0].booker").value(bookingResponseDto.getBooker()));
    }

    private BookingResponseDto getBookingResponseDto() {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(1L);
        bookingResponseDto.setStart(start);
        bookingResponseDto.setEnd(end);
        Item item = new Item();
        item.setId(1L);
        item.setName("test item");
        item.setDescription("test item description");
        bookingResponseDto.setItem(item);
        User booker = new User();
        booker.setId(1L);
        booker.setName("test user");
        booker.setEmail("test@test.com");
        bookingResponseDto.setBooker(booker);
        bookingResponseDto.setStatus(BookingStatus.WAITING);
        return bookingResponseDto;
    }

    private BookingRequestDto getBookingRequestDto() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);
        bookingRequestDto.setBookerId(1L);
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStatus(BookingStatus.WAITING);
        return bookingRequestDto;
    }
}