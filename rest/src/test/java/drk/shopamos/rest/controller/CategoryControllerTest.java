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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
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

import java.util.List;

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

    @Test
    @DisplayName("deleteCategory - when user is admin then call service layer and return 200 ")
    void deleteCategory_whenUserIsAdmin_return200() throws Exception {
        getMvc().send(DELETE, CATEGORY_URI_WITH_ID, FRUIT_CAT_ID)
                .withJwt(adminToken(LUFFY_ID))
                .thenExpectStatus(OK);

        verify(categoryService).deleteCategory(FRUIT_CAT_ID);
    }

    @Test
    @DisplayName("deleteCategory - when user is customer then return 403")
    void deleteCategory_whenUserIsCustomer_return403() throws Exception {
        getMvc().send(DELETE, CATEGORY_URI_WITH_ID, FRUIT_CAT_ID)
                .withJwt(customerToken(LUFFY_ID))
                .thenExpectStatus(FORBIDDEN);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName(
            "getCategory -  when user is authenticated then call service layer and return 200 response")
    void getCategory_whenUserAuthenticated_thenReturn200(boolean isAdmin) throws Exception {
        Category fruitCategory = buildFruitCategory();
        when(categoryService.getCategory(FRUIT_CAT_ID)).thenReturn(fruitCategory);
        CategoryResponse categoryResponse =
                getMvc().send(GET, CATEGORY_URI_WITH_ID, FRUIT_CAT_ID)
                        .withJwt(token(LUFFY_ID, isAdmin))
                        .thenExpectStatus(OK)
                        .getResponseBody(CategoryResponse.class);

        assertFruitCategoryResponse(categoryResponse);
    }

    @Test
    @DisplayName("getCategory - when user not authenticated then return 403")
    void getCategory_whenUserNotAuthenticated_thenReturn403() throws Exception {
        getMvc().send(GET, CATEGORY_URI_WITH_ID, FRUIT_CAT_ID).thenExpectStatus(FORBIDDEN);
    }

    @Test
    @DisplayName("getCategories - when user not authenticated then return 403")
    void getCategories_whenUserNotAuthenticated_thenReturn403() throws Exception {
        getMvc().send(GET, CATEGORY_URI).thenExpectStatus(FORBIDDEN);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName(
            "getCategories - when user is authenticated then call service layer and return 200 response")
    void getCategories_whenUserAuthenticated_thenReturn200(boolean isAdmin) throws Exception {
        List<Category> categories = List.of(buildFruitCategory());
        when(categoryService.getCategories(FRUIT_CAT_NAME, FRUIT_CAT_DESC)).thenReturn(categories);
        CategoryResponse[] categoryResponse =
                getMvc().send(GET, CATEGORY_URI)
                        .withQueryParam("name", FRUIT_CAT_NAME)
                        .withQueryParam("description", FRUIT_CAT_DESC)
                        .withJwt(token(LUFFY_ID, isAdmin))
                        .thenExpectStatus(OK)
                        .getResponseBody(CategoryResponse[].class);

        assertThat(categoryResponse.length, is(1));
        assertFruitCategoryResponse(categoryResponse[0]);
    }

    @Test
    @DisplayName(
            "getCategories - when query params are all null then call service layer and return 200 response")
    void getCategories_whenAttributesAreNull_thenReturn200() throws Exception {
        List<Category> categories = List.of(buildFruitCategory());
        when(categoryService.getCategories(null, null)).thenReturn(categories);
        CategoryResponse[] categoryResponse =
                getMvc().send(GET, CATEGORY_URI)
                        .withJwt(adminToken(LUFFY_ID))
                        .thenExpectStatus(OK)
                        .getResponseBody(CategoryResponse[].class);

        assertThat(categoryResponse.length, is(1));
        assertFruitCategoryResponse(categoryResponse[0]);
    }

    private void assertFruitCategoryResponse(CategoryResponse category) {
        assertThat(category.getId(), is(FRUIT_CAT_ID));
        assertThat(category.getName(), is(FRUIT_CAT_NAME));
        assertThat(category.getDescription(), is(FRUIT_CAT_DESC));
        assertThat(category.getIconUrl(), is(FRUIT_ICON_URL));
    }
}
