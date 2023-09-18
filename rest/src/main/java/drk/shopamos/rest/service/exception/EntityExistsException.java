package drk.shopamos.rest.service.exception;

import drk.shopamos.rest.config.MessageProvider;

import static drk.shopamos.rest.config.MessageProvider.MSG_ENTITY_EXISTS;

public class EntityExistsException extends BusinessException {
    public EntityExistsException(MessageProvider messageProvider, String entityName) {
        super(messageProvider.getMessage(MSG_ENTITY_EXISTS, entityName));
    }
}
