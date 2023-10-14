package drk.shopamos.rest.mother;

import drk.shopamos.rest.controller.request.AccountRequest;
import drk.shopamos.rest.model.entity.Account;

public class AccountMother {
    public static final Integer NAMI_ID = 3;
    public static final String NAMI_EMAIL = "nami@mugiwara.com";
    public static final String NAMI_NAME = "Nami";
    public static final String NAMI_PWD = "Oranges123";
    public static final Integer LUFFY_ID = 1;
    public static final String LUFFY_EMAIL = "luffy@mugiwara.com";
    public static final String LUFFY_NAME = "Monkey D Luffy";
    public static final String LUFFY_PWD = "Meat1234";
    public static final String LUFFY_ENCODED_PWD = "meat_encoded";
    public static final String ZORO_EMAIL = "zoro@mugiwara.com";
    public static final Integer VIVI_ID = 10;
    public static final String VIVI_EMAIL = "vivi@mugiwara.com";

    public static Account buildCustomerNamiWithId() {
        Account account = buildCustomerNami();
        account.setId(NAMI_ID);
        return account;
    }

    public static Account buildCustomerNamiWithoutId() {
        return buildCustomerNami();
    }

    private static Account buildCustomerNami() {
        Account account = new Account();
        account.setName(NAMI_NAME);
        account.setEmail(NAMI_EMAIL);
        account.setPassword(NAMI_PWD);
        account.setActive(true);
        account.setAdmin(false);
        return account;
    }

    public static Account buildAdminLuffy() {
        Account account = new Account();
        account.setId(LUFFY_ID);
        account.setName(LUFFY_NAME);
        account.setEmail(LUFFY_EMAIL);
        account.setPassword(LUFFY_PWD);
        account.setActive(true);
        account.setAdmin(true);
        return account;
    }

    public static Account buildAdminLuffyWithEncodedPwd() {
        Account account = buildAdminLuffy();
        account.setPassword(LUFFY_ENCODED_PWD);
        return account;
    }

    public static AccountRequest buildCustomerRequestNami() {
        return AccountRequest.builder()
                .name(NAMI_NAME)
                .email(NAMI_EMAIL)
                .password(NAMI_PWD)
                .build();
    }
}
