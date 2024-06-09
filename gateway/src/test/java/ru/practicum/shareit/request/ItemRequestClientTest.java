package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;

import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestClientTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    private ItemRequestClient itemRequestClient;

    @BeforeEach
    public void setup() {
        when(restTemplateBuilder.uriTemplateHandler(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.requestFactory(any(Supplier.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        itemRequestClient = new ItemRequestClient("http://test-server-url", restTemplateBuilder);
    }

    @Test
    public void addRequest_IsValid_ReturnsResponseEntity() {
        // given
        ItemRequestRequestDto itemRequestRequestDto = new ItemRequestRequestDto();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<Object> actualResponse = itemRequestClient.addRequest(1L, itemRequestRequestDto);

        // then
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getMyRequests_IsValid_ReturnsResponseEntity() {
        // given
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<Object> actualResponse = itemRequestClient.getMyRequests(1L);

        // then
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getAllRequests_IsValid_ReturnsResponseEntity() {
        // given
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class), any(Map.class)))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<Object> actualResponse = itemRequestClient.getAllRequests(1L, 0, 10);

        // then
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getRequest_IsValid_ReturnsResponseEntity() {
        // given
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<Object> actualResponse = itemRequestClient.getRequest(1L, 1L);

        // then
        assertEquals(expectedResponse, actualResponse);
    }
}
