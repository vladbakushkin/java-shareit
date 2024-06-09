package ru.practicum.shareit.converter;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.booking.model.BookingState;

public class StringToBookingStateConverter implements Converter<String, BookingState> {
    @Override
    public BookingState convert(String source) {
        return BookingState.valueOf(source.toUpperCase());
    }
}
