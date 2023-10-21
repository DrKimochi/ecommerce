package drk.shopamos.rest.controller;

import static drk.shopamos.rest.mother.AccountMother.LUFFY_ID;
import static drk.shopamos.rest.mother.CategoryMother.FRUIT_CAT_DESC;
import static drk.shopamos.rest.mother.CategoryMother.FRUIT_CAT_ID;
import static drk.shopamos.rest.mother.CategoryMother.FRUIT_CAT_NAME;
import static drk.shopamos.rest.mother.CategoryMother.FRUIT_ICON_URL;
import static drk.shopamos.rest.mother.CategoryMother.buildFruitCategory;
import static drk.shopamos.rest.mother.CategoryMother.buildFruitCategoryRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

import drk.shopamos.rest.argument.CategoryCreateUpdateUriArguments;
import drk.shopamos.rest.controller.mapper.CategoryMapperImpl;
import drk.shopamos.rest.controller.request.CategoryRequest;
import drk.shopamos.rest.controller.response.CategoryResponse;
import drk.shopamos.rest.controller.response.ErrorResponse;
import drk.shopamos.rest.model.entity.Category;
import drk.shopamos.rest.service.CategoryService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;

@WebMvcTest
@ContextConfiguration(classes = {CategoryController.class, CategoryMapperImpl.class})
public class CategoryControllerTest extends ControllerTest {

    public static final String CATEGORY_URI = "/v1/categories";
    public static final String CATEGORY_URI_WITH_ID = "/v1/categories/{id}";
    @MockBean protected CategoryService categoryService;

    @ParameterizedTest
    @ArgumentsSource(CategoryCreateUpdateUriArguments.class)
    @DisplayName("createUpdateCategory - when body is missing then return 400 with message")
    void createUpdateCategory_whenBodyMissing_thenReturn400(
            HttpMethod httpMethod, String uri, String uriVariable) throws Exception {
        ErrorResponse errorResponse =
                getMvc().send(httpMethod, uri, uriVariable)
                        .withJwt(customerToken(LUFFY_ID))
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);
        errorResponseAssert.requestBodyUnreadable(errorResponse);
    }

    @Test
    @DisplayName("createCategory - when id and name missing then return 400 with message")
    void createCategory_whenIdAndNameMissing_thenReturn400() throws Exception {
        CategoryRequest request = new CategoryRequest();
        ErrorResponse errorResponse =
                getMvc().send(POST, CATEGORY_URI)
                        .withBody(request)
                        .withJwt(customerToken(LUFFY_ID))
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);

        errorResponseAssert.emptyField(errorResponse, "id");
        errorResponseAssert.emptyField(errorResponse, "name");
    }

    @ParameterizedTest
    @ValueSource(strings = {"FRUIT", "FRU", "FrUT"})
    @DisplayName("createCategory - when id is not 4 uppercase letters then return 400 with message")
    void createCategory_whenIdNot4Characters_thenReturn400(String id) throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setId(id);
        ErrorResponse errorResponse =
                getMvc().send(POST, CATEGORY_URI)
                        .withBody(request)
                        .withJwt(customerToken(LUFFY_ID))
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);

        errorResponseAssert.categoryField(errorResponse);
    }

    @Test
    @DisplayName("createCategory - when valid category then call service layer and return 200")
    void createCategory_whenValidData_thenReturn200() throws Exception {
        CategoryRequest categoryRequest = buildFruitCategoryRequest();
        Category category = buildFruitCategory();
        when(categoryService.createCategory(category)).thenReturn(category);
        CategoryResponse categoryResponse =
                getMvc().send(POST, CATEGORY_URI)
                        .withBody(categoryRequest)
                        .withJwt(adminToken(LUFFY_ID))
                        .thenExpectStatus(OK)
                        .getResponseBody(CategoryResponse.class);

        assertFruitCategoryResponse(categoryResponse);
    }

    @ParameterizedTest
    @ArgumentsSource(CategoryCreateUpdateUriArguments.class)
    @DisplayName("createUpdateCategory - when user is not admin role then return 403")
    void createCategory_whenNotAdmin_thenReturn403(
            HttpMethod httpMethod, String uri, String uriVariable) throws Exception {
        getMvc().send(httpMethod, uri, uriVariable)
                .withBody(buildFruitCategoryRequest())
                .withJwt(customerToken(LUFFY_ID))
                .thenExpectStatus(FORBIDDEN);
    }

    @Test
    @DisplayName("updateCategory - when name missing then return 400 with message")
    void updateCategory_whenNameMissing_thenReturn400() throws Exception {
        CategoryRequest categoryRequest = buildFruitCategoryRequest();
        categoryRequest.setName(null);
        ErrorResponse errorResponse =
                getMvc().send(PUT, CATEGORY_URI_WITH_ID, FRUIT_CAT_ID)
                        .withBody(categoryRequest)
                        .withJwt(customerToken(LUFFY_ID))
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);

        errorResponseAssert.emptyField(errorResponse, "name");
    }

    @Test
    @DisplayName("updateCategory - when valid data then return 200 with updated category")
    void updateCategory_whenValidData_thenReturn200() throws Exception {
        Category category = buildFruitCategory();
        CategoryRequest categoryRequest = buildFruitCategoryRequest();
        categoryRequest.setId(null);
        when(categoryService.updateCategory(category)).thenReturn(category);
        CategoryResponse categoryResponse =
                getMvc().send(PUT, CATEGORY_URI_WITH_ID, FRUIT_CAT_ID)
                        .withBody(categoryRequest)
                        .withJwt(adminToken(LUFFY_ID))
                        .thenExpectStatus(OK)
                        .getResponseBody(CategoryResponse.class);

        assertFruitCategoryResponse(categoryResponse);
    }

    private void assertFruitCategoryResponse(CategoryResponse category) {
        assertThat(category.getId(), is(FRUIT_CAT_ID));
        assertThat(category.getName(), is(FRUIT_CAT_NAME));
        assertThat(category.getDescription(), is(FRUIT_CAT_DESC));
        assertThat(category.getIconUrl(), is(FRUIT_ICON_URL));
    }
}
