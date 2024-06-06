package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

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
    private BookingClient bookingClient;

    @SneakyThrows
    @Test
    void addBooking_IsValid_ReturnsResponseEntity() {
        // given
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Long itemId = 1L;
        BookingRequestDto bookingRequestDto = new BookingRequestDto(start, end, itemId);
        when(bookingClient.addBooking(anyLong(), any(BookingRequestDto.class)))
                .thenReturn(new ResponseEntity<>(bookingRequestDto, HttpStatus.OK));

        // then
        mockMvc.perform(
                        post("/bookings")
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.itemId").value(itemId));
    }

    @SneakyThrows
    @Test
    void addBooking_startIsPast_ThrowsBadRequestException() {
        // given
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
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
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
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
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
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
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
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
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
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
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
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
    void handleBooking_IsValid_ReturnsResponseEntity() {
        // given
        boolean approved = true;
        when(bookingClient.handleBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        // then
        mockMvc.perform(
                        patch("/bookings/{bookingId}", 1L)
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void getBooking_IsValid_ReturnsResponseEntity() {
        // given
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Long itemId = 1L;
        BookingRequestDto bookingRequestDto = new BookingRequestDto(start, end, itemId);
        when(bookingClient.getBooking(anyLong(), anyLong())).thenReturn(new ResponseEntity<>(bookingRequestDto, HttpStatus.OK));

        // then
        mockMvc.perform(
                        get("/bookings/{bookingId}", 1L)
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.itemId").value(itemId));
    }

    @SneakyThrows
    @Test
    void getAllBookingsForUser_IsValid_ReturnsResponseEntity() {
        // given
        when(bookingClient.getAllBookingsForUser(1L, BookingState.valueOf("WAITING"), 0, 10))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        // then
        mockMvc.perform(
                        get("/bookings")
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("state", "all")
                                .param("from", "0")
                                .param("size", "10"))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void getAllBookings_pageArgumentsWrong_ThrowsConstraintViolationException() {
        // then
        mockMvc.perform(
                        get("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("state", "WAITING")
                                .param("from", String.valueOf(-1))
                                .param("size", String.valueOf(10)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        get("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("state", "WAITING")
                                .param("from", String.valueOf(0))
                                .param("size", String.valueOf(0)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllBookingsForUserItems_IsValid_ReturnsResponseEntity() {
        // given
        when(bookingClient.getAllBookingsForUserItems(1L, BookingState.valueOf("WAITING"), 0, 10))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        // then
        mockMvc.perform(
                        get("/bookings/owner")
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("state", "WAITING")
                                .param("from", "0")
                                .param("size", "10"))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void getAllBookingsForUserItems_pageArgumentsWrong_ThrowsConstraintViolationException() {
        // then
        mockMvc.perform(
                        get("/bookings/owner")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("state", "WAITING")
                                .param("from", String.valueOf(-1))
                                .param("size", String.valueOf(10)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        get("/bookings/owner")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("state", "WAITING")
                                .param("from", String.valueOf(0))
                                .param("size", String.valueOf(0)))
                .andExpect(status().isBadRequest());
    }
}