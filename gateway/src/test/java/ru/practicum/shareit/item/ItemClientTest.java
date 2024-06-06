package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemClientTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    private ItemClient itemClient;

    @BeforeEach
    public void setup() {
        when(restTemplateBuilder.uriTemplateHandler(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.requestFactory(any(Supplier.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        itemClient = new ItemClient("http://test-server-url", restTemplateBuilder);
    }

    @Test
    public void addItem_IsValid_ReturnsResponseEntity() {
        // given
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<Object> actualResponse = itemClient.addItem(1L, itemRequestDto);

        // then
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void updateItem_IsValid_ReturnsResponseEntity() {
        // given
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<Object> actualResponse = itemClient.updateItem(1L, 1L, itemUpdateDto);

        // then
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getItem_IsValid_ReturnsResponseEntity() {
        // given
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<Object> actualResponse = itemClient.getItem(1L, 1L);

        // then
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getAllItemsByOwner_IsValid_ReturnsResponseEntity() {
        // given
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class), any(Map.class)))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<Object> actualResponse = itemClient.getAllItemsByOwner(1L, 0, 10);

        // then
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void searchAvailableItem_IsValid_ReturnsResponseEntity() {
        // given
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class), any(Map.class)))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<Object> actualResponse = itemClient.searchAvailableItem("text", 0, 10);

        // then
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void addComment_IsValid_ReturnsResponseEntity() {
        // given
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<Object> actualResponse = itemClient.addComment(1L, 1L, commentRequestDto);

        // then
        assertEquals(expectedResponse, actualResponse);
    }
}
