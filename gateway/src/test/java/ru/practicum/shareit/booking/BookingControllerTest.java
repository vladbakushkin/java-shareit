package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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