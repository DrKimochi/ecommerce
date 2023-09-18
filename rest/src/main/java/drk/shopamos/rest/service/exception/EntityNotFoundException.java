package drk.shopamos.rest.service.exception;

import static drk.shopamos.rest.config.MessageProvider.MSG_ENTITY_NOT_FOUND;

import drk.shopamos.rest.config.MessageProvider;

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(MessageProvider messageProvider, String entityName) {
        super(messageProvider.getMessage(MSG_ENTITY_NOT_FOUND, entityName));
    }
}
