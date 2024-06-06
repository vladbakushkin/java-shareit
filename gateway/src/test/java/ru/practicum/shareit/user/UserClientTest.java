package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.dto.UserNewDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserClientTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    private UserClient userClient;

    @BeforeEach
    public void setup() {
        when(restTemplateBuilder.uriTemplateHandler(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.requestFactory(any(Supplier.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        userClient = new UserClient("http://test-server-url", restTemplateBuilder);
    }

    @Test
    public void createUser_IsValid_ReturnsResponseEntity() {
        // given
        UserNewDto userNewDto = new UserNewDto();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<Object> actualResponse = userClient.createUser(userNewDto);

        // then
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void updateUser_IsValid_ReturnsResponseEntity() {
        // given
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<Object> actualResponse = userClient.updateUser(1L, userUpdateDto);

        // then
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getUser_IsValid_ReturnsResponseEntity() {
        // given
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<Object> actualResponse = userClient.getUser(1L);

        // then
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getAllUsers_IsValid_ReturnsResponseEntity() {
        // given
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class), any(Map.class)))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<Object> actualResponse = userClient.getAllUsers(0, 10);

        // then
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void deleteUser_IsValid_ReturnsResponseEntity() {
        // given
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
                .thenReturn(expectedResponse);

        // when
        ResponseEntity<Object> actualResponse = userClient.deleteUser(1L);

        // then
        assertEquals(expectedResponse, actualResponse);
    }
}
