package drk.shopamos.rest.mother;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import drk.shopamos.rest.model.entity.Account;

public class AccountMother {

    public static final String NAMI_EMAIL = "nami@mugiwara.com";
    public static final String NAMI_NAME = "Nami";
    public static final String NAMI_PWD = "oranges123";
    public static final String LUFFY_EMAIL = "luffy@mugiwara.com";
    public static final String LUFFY_NAME = "Monkey D Luffy";
    public static final String LUFFY_PWD = "meat123";
    public static final String ZORO_EMAIL = "zoro@mugiwara.com";
    public static final String VIVI_EMAIL = "vivi@mugiwara.com";

    public static Account buildAccountNami() {
        Account account = new Account();
        account.setName(NAMI_NAME);
        account.setEmail(NAMI_EMAIL);
        account.setPassword(NAMI_PWD);
        account.setActive(true);
        account.setAdmin(true);
        return account;
    }

    public static void assertAccountDataNami(Account account) {
        assertThat(account.getId(), is(notNullValue()));
        assertThat(account.getEmail(), is(NAMI_EMAIL));
        assertThat(account.getUsername(), is(NAMI_EMAIL));
        assertThat(account.getPassword(), is(NAMI_PWD));
        assertThat(account.getName(), is(NAMI_NAME));
        assertThat(account.isActive(), is(true));
        assertThat(account.isAdmin(), is(true));
    }

    public static void assertAccountDataLuffy(Account account) {
        assertThat(account.getId(), notNullValue());
        assertThat(account.getEmail(), is(LUFFY_EMAIL));
        assertThat(account.getName(), is(LUFFY_NAME));
        assertThat(account.getUsername(), is(LUFFY_EMAIL));
        assertThat(account.getPassword(), is(LUFFY_PWD));
        assertThat(account.isAdmin(), is(true));
        assertThat(account.isActive(), is(true));
    }
}
