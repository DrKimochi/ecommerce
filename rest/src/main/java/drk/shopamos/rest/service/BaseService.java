package drk.shopamos.rest.service;

import static drk.shopamos.rest.config.MessageProvider.MSG_NOT_FOUND_ID;

import drk.shopamos.rest.config.MessageProvider;
import drk.shopamos.rest.service.exception.BadDataException;
import drk.shopamos.rest.service.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.function.Supplier;

@RequiredArgsConstructor
public abstract class BaseService {

    private final MessageProvider msgProvider;
    private final Clock clock;

    protected Supplier<EntityNotFoundException> anEntityNotFoundException(Integer id) {
        return () -> new EntityNotFoundException(msgProvider.getMessage(MSG_NOT_FOUND_ID, id));
    }

    protected Supplier<BadDataException> aBadDataException(String message) {
        return () -> new BadDataException(msgProvider.getMessage(message));
    }

    protected LocalDateTime now() {
        return LocalDateTime.ofInstant(clock.instant(), clock.getZone());
    }
}
