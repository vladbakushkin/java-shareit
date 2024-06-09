package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingState {
    // Все
    ALL,
    // Текущие
    CURRENT,
    // Завершенные
    PAST,
    // Будущие
    FUTURE,
    // Ожидающие подтверждения
    WAITING,
    // Отклоненные
    REJECTED;

    public static Optional<BookingState> from(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
