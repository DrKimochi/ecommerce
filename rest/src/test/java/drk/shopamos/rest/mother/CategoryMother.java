package drk.shopamos.rest.mother;

import drk.shopamos.rest.model.entity.Category;

public class CategoryMother {
    public static final String FRUIT_CAT_ID = "FRUT";
    public static final String FRUIT_CAT_NAME = "Devil Fruits";
    public static final String FRUIT_CAT_DESC = "Powerful not-so-delicious Devil Fruits!";
    public static final String FRUIT_ICON_URL = "http://localImageHost/dfruit.ico";

    public static Category buildFruitCategory() {
        Category category = new Category();
        category.setId(FRUIT_CAT_ID);
        category.setName(FRUIT_CAT_NAME);
        category.setDescription(FRUIT_CAT_DESC);
        category.setIconUrl(FRUIT_ICON_URL);
        return category;
    }
}
