package drk.shopamos.rest.mother;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import drk.shopamos.rest.controller.request.AccountRequest;
import drk.shopamos.rest.controller.response.AccountResponse;
import drk.shopamos.rest.model.entity.Account;

public class AccountMother {
    public static final Integer NAMI_ID = 2;
    public static final String NAMI_EMAIL = "nami@mugiwara.com";
    public static final String NAMI_NAME = "Nami";
    public static final String NAMI_PWD = "Oranges123";
    public static final Integer LUFFY_ID = 1;

    public static final String LUFFY_EMAIL = "luffy@mugiwara.com";
    public static final String LUFFY_NAME = "Monkey D Luffy";
    public static final String LUFFY_PWD = "Meat1234";
    public static final String ZORO_EMAIL = "zoro@mugiwara.com";
    public static final String VIVI_EMAIL = "vivi@mugiwara.com";

    public static Account buildCustomerNami() {
        Account account = buildNewCustomerNami();
        account.setId(NAMI_ID);
        return account;
    }

    public static Account buildNewCustomerNami() {
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

    public static AccountRequest buildCustomerRequestNami() {
        return AccountRequest.builder()
                .name(NAMI_NAME)
                .email(NAMI_EMAIL)
                .password(NAMI_PWD)
                .active(true)
                .admin(false)
                .build();
    }

    public static void assertAccountNami(Account account) {
        assertThat(account.getEmail(), is(NAMI_EMAIL));
        assertThat(account.getUsername(), is(NAMI_EMAIL));
        assertThat(account.getPassword(), is(NAMI_PWD));
        assertThat(account.getName(), is(NAMI_NAME));
        assertThat(account.isActive(), is(true));
        assertThat(account.isAdmin(), is(false));
    }

    public static void assertAccountResponseNami(AccountResponse accountResponse) {
        assertThat(accountResponse.getEmail(), is(NAMI_EMAIL));
        assertThat(accountResponse.getId(), is(NAMI_ID));
        assertThat(accountResponse.getName(), is(NAMI_NAME));
        assertThat(accountResponse.getAdmin(), is(false));
        assertThat(accountResponse.getActive(), is(true));
    }

    public static void assertAccountLuffy(Account account) {
        assertThat(account.getId(), notNullValue());
        assertThat(account.getEmail(), is(LUFFY_EMAIL));
        assertThat(account.getName(), is(LUFFY_NAME));
        assertThat(account.getUsername(), is(LUFFY_EMAIL));
        assertThat(account.getPassword(), is(LUFFY_PWD));
        assertThat(account.isAdmin(), is(true));
        assertThat(account.isActive(), is(true));
    }
}
