package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingClientTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    private BookingClient bookingClient;

    @BeforeEach
    public void setup() {
        when(restTemplateBuilder.uriTemplateHandler(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.requestFactory(any(Supplier.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        bookingClient = new BookingClient("http://test-server-url", restTemplateBuilder);
    }

    @Test
    public void addBooking_IsValid_ReturnsResponseEntity() {
        // given
        BookingRequestDto requestDto = new BookingRequestDto();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<Object> actualResponse = bookingClient.addBooking(1L, requestDto);

        // then
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void handleBooking_IsValid_ReturnsResponseEntity() {
        // given
        boolean approved = true;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<Object> actualResponse = bookingClient.handleBooking(1L, 1L, approved);

        // then
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getBooking_IsValid_ReturnsResponseEntity() {
        // given
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<Object> actualResponse = bookingClient.getBooking(1L, 1L);

        // then
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getAllBookingsForUser_IsValid_ReturnsResponseEntity() {
        // given
        BookingState state = BookingState.WAITING;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class), any(Map.class)))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<Object> actualResponse = bookingClient.getAllBookingsForUser(1L, state, 0, 10);

        // then
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getAllBookingsForUserItems_IsValid_ReturnsResponseEntity() {
        // given
        BookingState state = BookingState.WAITING;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class), any(Map.class)))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<Object> actualResponse = bookingClient.getAllBookingsForUserItems(1L, state, 0, 10);

        // then
        assertEquals(expectedResponse, actualResponse);
    }
}
