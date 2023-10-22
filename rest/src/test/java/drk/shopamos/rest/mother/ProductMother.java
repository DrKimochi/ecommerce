package drk.shopamos.rest.mother;

import static drk.shopamos.rest.mother.CategoryMother.buildShipCategory;

import drk.shopamos.rest.model.entity.Product;

import java.math.BigDecimal;

public class ProductMother {
    public static final Integer TSUNNY_PROD_ID = 10;
    public static final String TSUNNY_PROD_NAME = "The Thousand Sunny";
    public static final String TSUNNY_PROD_DESC =
            "A ship made from the finest wood built by the greatest shipwrights";
    public static final BigDecimal TSUNNY_PROD_PRICE = BigDecimal.valueOf(1337420.69);

    public static final String TSUNNY_PROD_IMAGEURL = "http://localImageRepo/thousandsunny.png";

    public static Product buildThousandSunny() {
        Product product = new Product();
        product.setId(TSUNNY_PROD_ID);
        product.setName(TSUNNY_PROD_NAME);
        product.setDescription(TSUNNY_PROD_DESC);
        product.setPrice(TSUNNY_PROD_PRICE);
        product.setImageUrl(TSUNNY_PROD_IMAGEURL);
        product.setCategory(buildShipCategory());
        return product;
    }
}
