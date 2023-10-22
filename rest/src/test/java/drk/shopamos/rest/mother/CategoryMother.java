package drk.shopamos.rest.mother;

import drk.shopamos.rest.controller.request.CategoryRequest;
import drk.shopamos.rest.model.entity.Category;

public class CategoryMother {
    public static final String FRUIT_CAT_ID = "FRUT";
    public static final String MISC_CAT_ID = "MISC";

    public static final String SHIP_CAT_ID = "SHIP";
    public static final String FRUIT_CAT_NAME = "Devil Fruits";
    public static final String MISC_CAT_NAME = "Miscellaneous";

    public static final String SHIP_CAT_NAME = "Ships";

    public static final String FRUIT_CAT_DESC = "Powerful not-so-delicious Devil Fruits!";
    public static final String MISC_CAT_DESC = "Products that dont fit in any other category";
    public static final String FRUIT_ICON_URL = "http://localImageHost/dfruit.ico";
    public static final String MISC_ICON_URL = "http://localImageRepo/misc.ico";

    public static Category buildMiscCategory() {
        Category category = new Category();
        category.setId(MISC_CAT_ID);
        category.setName(MISC_CAT_NAME);
        category.setDescription(MISC_CAT_DESC);
        category.setIconUrl(MISC_ICON_URL);
        return category;
    }

    public static Category buildFruitCategory() {
        Category category = new Category();
        category.setId(FRUIT_CAT_ID);
        category.setName(FRUIT_CAT_NAME);
        category.setDescription(FRUIT_CAT_DESC);
        category.setIconUrl(FRUIT_ICON_URL);
        return category;
    }

    public static Category buildShipCategory() {
        Category category = new Category();
        category.setId(SHIP_CAT_ID);
        return category;
    }

    public static CategoryRequest buildFruitCategoryRequest() {
        CategoryRequest category = new CategoryRequest();
        category.setId(FRUIT_CAT_ID);
        category.setName(FRUIT_CAT_NAME);
        category.setDescription(FRUIT_CAT_DESC);
        category.setIconUrl(FRUIT_ICON_URL);
        return category;
    }
}
