package drk.shopamos.rest.controller;

import drk.shopamos.rest.model.entity.Account;

import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {

    protected Account getPrincipal() {
        return (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
