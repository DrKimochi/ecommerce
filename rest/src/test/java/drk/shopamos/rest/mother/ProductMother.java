package drk.shopamos.rest.mother;

import static drk.shopamos.rest.mother.CategoryMother.SHIP_CAT_ID;
import static drk.shopamos.rest.mother.CategoryMother.buildShipCategory;
import static drk.shopamos.rest.mother.CategoryMother.buildSwordCategory;

import drk.shopamos.rest.controller.request.ProductRequest;
import drk.shopamos.rest.model.entity.Product;

import java.math.BigDecimal;

public class ProductMother {
    public static final Integer TSUNNY_PROD_ID = 10;
    public static final String TSUNNY_PROD_NAME = "The Thousand Sunny";
    public static final String TSUNNY_PROD_DESC =
            "A ship made from the finest wood built by the greatest shipwrights";
    public static final BigDecimal TSUNNY_PROD_PRICE = BigDecimal.valueOf(1337420.69);
    public static final String TSUNNY_PROD_IMAGEURL = "http://localImageRepo/thousandsunny.png";

    public static final Integer SHUSUI_PROD_ID = 1;
    public static final String SHUSUI_PROD_NAME = "Shusui";
    public static final String SHUSUI_PROD_DESC =
            "Famous katana once wielded by the legendary samurai";
    public static final BigDecimal SHUSUI_PROD_PRICE = BigDecimal.valueOf(3493.83);
    public static final String SHUSUI_PROD_IMAGEURL = "http://localImageRepo/shusui.jpg";

    public static final Integer GMERRY_PROD_ID = 2;
    public static final String GMERRY_PROD_NAME = "The Going Merry";
    public static final String GMERRY_PROD_DESC =
            "A worthy ship for anyone willing to become the next pirate king";
    public static final BigDecimal GMERRY_PROD_PRICE = BigDecimal.valueOf(9998.99);
    public static final String GMERRY_PROD_IMAGEURL = "http://localImageRepo/merry.jpg";

    public static Product buildThousandSunny() {
        Product product = new Product();
        product.setId(TSUNNY_PROD_ID);
        product.setName(TSUNNY_PROD_NAME);
        product.setDescription(TSUNNY_PROD_DESC);
        product.setPrice(TSUNNY_PROD_PRICE);
        product.setImageUrl(TSUNNY_PROD_IMAGEURL);
        product.setActive(true);
        product.setCategory(buildShipCategory());
        return product;
    }

    public static ProductRequest buildThousandSunnyRequest() {
        return ProductRequest.builder()
                .categoryId(SHIP_CAT_ID)
                .name(TSUNNY_PROD_NAME)
                .description(TSUNNY_PROD_DESC)
                .price(TSUNNY_PROD_PRICE)
                .imageUrl(TSUNNY_PROD_IMAGEURL)
                .isActive(true)
                .build();
    }

    public static Product buildShusui() {
        Product product = new Product();
        product.setId(SHUSUI_PROD_ID);
        product.setName(SHUSUI_PROD_NAME);
        product.setDescription(SHUSUI_PROD_DESC);
        product.setPrice(SHUSUI_PROD_PRICE);
        product.setImageUrl(SHUSUI_PROD_IMAGEURL);
        product.setActive(true);
        product.setCategory(buildSwordCategory());
        return product;
    }

    public static Product buildGoingMerry() {
        Product product = new Product();
        product.setId(GMERRY_PROD_ID);
        product.setName(GMERRY_PROD_NAME);
        product.setDescription(GMERRY_PROD_DESC);
        product.setPrice(GMERRY_PROD_PRICE);
        product.setImageUrl(GMERRY_PROD_IMAGEURL);
        product.setActive(false);
        product.setCategory(buildShipCategory());
        return product;
    }
}
