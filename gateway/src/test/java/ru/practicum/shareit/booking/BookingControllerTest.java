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
        when(bookingClient.addBooking(1L, bookingRequestDto))
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
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Long itemId = 1L;
        BookingRequestDto bookingRequestDto = new BookingRequestDto(start, end, itemId);

        // then
        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"start\" Причина: \"must be a date in the present or in the future\""));
    }

    @SneakyThrows
    @Test
    void addBooking_startIsNull_ThrowsBadRequestException() {
        // given
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Long itemId = 1L;
        BookingRequestDto bookingRequestDto = new BookingRequestDto(null, end, itemId);

        // then
        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"start\" Причина: \"must not be null\""));
    }

    @SneakyThrows
    @Test
    void addBooking_endIsPast_ThrowsBadRequestException() {
        // given
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().minusHours(1);
        Long itemId = 1L;
        BookingRequestDto bookingRequestDto = new BookingRequestDto(start, end, itemId);

        // then
        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"end\" Причина: \"must be a future date\""));
    }

    @SneakyThrows
    @Test
    void addBooking_endIsPresent_ThrowsBadRequestException() {
        // given
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now();
        Long itemId = 1L;
        BookingRequestDto bookingRequestDto = new BookingRequestDto(start, end, itemId);

        // then
        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"end\" Причина: \"must be a future date\""));
    }

    @SneakyThrows
    @Test
    void addBooking_endIsNull_ThrowsBadRequestException() {
        // given
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        Long itemId = 1L;
        BookingRequestDto bookingRequestDto = new BookingRequestDto(start, null, itemId);

        // then
        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"end\" Причина: \"must not be null\""));
    }

    @SneakyThrows
    @Test
    void addBooking_itemIdIsNull_ThrowsBadRequestException() {
        // given
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto bookingRequestDto = new BookingRequestDto(start, end, null);

        // then
        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"itemId\" Причина: \"must not be null\""));
    }

    @SneakyThrows
    @Test
    void handleBooking_IsValid_ReturnsResponseEntity() {
        // given
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Long itemId = 1L;
        BookingRequestDto bookingRequestDto = new BookingRequestDto(start, end, itemId);
        boolean approved = true;
        when(bookingClient.handleBooking(1L, 1L, approved))
                .thenReturn(new ResponseEntity<>(bookingRequestDto, HttpStatus.OK));

        // then
        mockMvc.perform(
                        patch("/bookings/{bookingId}", 1L)
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.itemId").value(itemId));
    }

    @SneakyThrows
    @Test
    void getBooking_IsValid_ReturnsResponseEntity() {
        // given
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Long itemId = 1L;
        BookingRequestDto bookingRequestDto = new BookingRequestDto(start, end, itemId);
        when(bookingClient.getBooking(1L, 1L))
                .thenReturn(new ResponseEntity<>(bookingRequestDto, HttpStatus.OK));

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
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Long itemId = 1L;
        BookingRequestDto bookingRequestDto = new BookingRequestDto(start, end, itemId);

        when(bookingClient.getAllBookingsForUser(1L, BookingState.valueOf("WAITING"), 0, 10))
                .thenReturn(new ResponseEntity<>(bookingRequestDto, HttpStatus.OK));

        // then
        mockMvc.perform(
                        get("/bookings")
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("state", "WAITING")
                                .param("from", "0")
                                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.itemId").value(itemId));
    }

    @SneakyThrows
    @Test
    void getAllBookingsForUser_pageArgumentsWrong_ThrowsConstraintViolationException() {
        // then
        mockMvc.perform(
                        get("/bookings")
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("state", "WAITING")
                                .param("from", String.valueOf(-1))
                                .param("size", String.valueOf(10)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"getAllBookingsForUser.from\" " +
                        "Причина: \"must be greater than or equal to 0\""));

        mockMvc.perform(
                        get("/bookings")
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("state", "WAITING")
                                .param("from", String.valueOf(0))
                                .param("size", String.valueOf(0)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"getAllBookingsForUser.size\" " +
                        "Причина: \"must be greater than 0\""));
    }

    @SneakyThrows
    @Test
    void getAllBookingsForUserItems_IsValid_ReturnsResponseEntity() {
        // given
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Long itemId = 1L;
        BookingRequestDto bookingRequestDto = new BookingRequestDto(start, end, itemId);

        when(bookingClient.getAllBookingsForUserItems(1L, BookingState.valueOf("WAITING"), 0, 10))
                .thenReturn(new ResponseEntity<>(bookingRequestDto, HttpStatus.OK));

        // then
        mockMvc.perform(
                        get("/bookings/owner")
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("state", "WAITING")
                                .param("from", "0")
                                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.itemId").value(itemId));
    }

    @SneakyThrows
    @Test
    void getAllBookingsForUserItems_pageArgumentsWrong_ThrowsConstraintViolationException() {
        // then
        mockMvc.perform(
                        get("/bookings/owner")
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("state", "WAITING")
                                .param("from", String.valueOf(-1))
                                .param("size", String.valueOf(10)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"getAllBookingsForUserItems.from\" " +
                        "Причина: \"must be greater than or equal to 0\""));

        mockMvc.perform(
                        get("/bookings/owner")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-SHARER-USER-ID", "1")
                                .param("state", "WAITING")
                                .param("from", String.valueOf(0))
                                .param("size", String.valueOf(0)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"getAllBookingsForUserItems.size\" " +
                        "Причина: \"must be greater than 0\""));
    }
}