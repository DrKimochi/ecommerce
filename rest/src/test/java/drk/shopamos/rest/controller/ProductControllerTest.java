package drk.shopamos.rest.controller;

import static drk.shopamos.rest.mother.AccountMother.LUFFY_ID;
import static drk.shopamos.rest.mother.CategoryMother.SHIP_CAT_ID;
import static drk.shopamos.rest.mother.ProductMother.TSUNNY_PROD_DESC;
import static drk.shopamos.rest.mother.ProductMother.TSUNNY_PROD_ID;
import static drk.shopamos.rest.mother.ProductMother.TSUNNY_PROD_IMAGEURL;
import static drk.shopamos.rest.mother.ProductMother.TSUNNY_PROD_NAME;
import static drk.shopamos.rest.mother.ProductMother.TSUNNY_PROD_PRICE;
import static drk.shopamos.rest.mother.ProductMother.buildThousandSunny;
import static drk.shopamos.rest.mother.ProductMother.buildThousandSunnyRequest;

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

import drk.shopamos.rest.argument.ProductCreateUpdateDeleteUriArguments;
import drk.shopamos.rest.argument.ProductCreateUpdateUriArguments;
import drk.shopamos.rest.controller.mapper.ProductMapperImpl;
import drk.shopamos.rest.controller.request.ProductRequest;
import drk.shopamos.rest.controller.response.ErrorResponse;
import drk.shopamos.rest.controller.response.ProductResponse;
import drk.shopamos.rest.model.entity.Product;
import drk.shopamos.rest.service.ProductService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.List;

@WebMvcTest
@ContextConfiguration(classes = {ProductController.class, ProductMapperImpl.class})
public class ProductControllerTest extends ControllerTest {
    public static final String PRODUCT_URI = "/v1/products";
    public static final String PRODUCT_URI_WITH_ID = "/v1/products/{id}";

    @MockBean protected ProductService productService;

    @ParameterizedTest
    @ArgumentsSource(ProductCreateUpdateUriArguments.class)
    @DisplayName("createUpdateProduct - when body missing then return 400 with message")
    void createUpdateProduct_whenBodyMissing_thenReturn400(
            HttpMethod httpMethod, String uri, Integer uriVariable) throws Exception {

        ErrorResponse errorResponse =
                getMvc().send(httpMethod, uri, uriVariable)
                        .withJwt(adminToken(LUFFY_ID))
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);
        errorResponseAssert.requestBodyUnreadable(errorResponse);
    }

    @ParameterizedTest
    @ArgumentsSource(ProductCreateUpdateUriArguments.class)
    @DisplayName(
            "createUpdateProduct - when categoryId, name or price missing then return 400 with message")
    void createUpdateProduct_whenCategoryId_name_price_missing_thenReturn400(
            HttpMethod httpMethod, String uri, Integer uriVariable) throws Exception {
        ProductRequest productRequest = new ProductRequest();
        ErrorResponse errorResponse =
                getMvc().send(httpMethod, uri, uriVariable)
                        .withBody(productRequest)
                        .withJwt(adminToken(LUFFY_ID))
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);

        errorResponseAssert.emptyField(errorResponse, "categoryId");
        errorResponseAssert.emptyField(errorResponse, "name");
        errorResponseAssert.emptyField(errorResponse, "price");
    }

    @ParameterizedTest
    @ArgumentsSource(ProductCreateUpdateUriArguments.class)
    @DisplayName("createUpdateProduct - when price less than zero then return 400 with message")
    void createUpdateProduct_whenPriceLessThanZero_thenReturn400(
            HttpMethod httpMethod, String uri, Integer uriVariable) throws Exception {
        ProductRequest productRequest = buildThousandSunnyRequest();
        productRequest.setPrice(BigDecimal.valueOf(-1));
        ErrorResponse errorResponse =
                getMvc().send(httpMethod, uri, uriVariable)
                        .withBody(productRequest)
                        .withJwt(adminToken(LUFFY_ID))
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);

        errorResponseAssert.positiveField(errorResponse, "price");
    }

    @ParameterizedTest
    @ArgumentsSource(ProductCreateUpdateUriArguments.class)
    @DisplayName(
            "createUpdateProduct - when name greater than 100 chars then return 400 with message")
    void createUpdateProduct_whenNameMoreThan100Chars_thenReturn400(
            HttpMethod httpMethod, String uri, Integer uriVariable) throws Exception {
        ProductRequest productRequest = buildThousandSunnyRequest();
        productRequest.setName(new String(new char[101]));
        ErrorResponse errorResponse =
                getMvc().send(httpMethod, uri, uriVariable)
                        .withBody(productRequest)
                        .withJwt(adminToken(LUFFY_ID))
                        .thenExpectStatus(BAD_REQUEST)
                        .getResponseBody(ErrorResponse.class);

        errorResponseAssert.maxLengthField(errorResponse, "name", "100");
    }

    @ParameterizedTest
    @ArgumentsSource(ProductCreateUpdateDeleteUriArguments.class)
    @DisplayName("createUpdateDeleteProduct - customers cannot create update or delete products")
    void createUpdateDeleteProduct_whenCustomerRole_thenReturn403(
            HttpMethod httpMethod, String uri, Integer uriVariable) throws Exception {
        ProductRequest productRequest = buildThousandSunnyRequest();
        getMvc().send(httpMethod, uri, uriVariable)
                .withBody(productRequest)
                .withJwt(customerToken(LUFFY_ID))
                .thenExpectStatus(FORBIDDEN);
    }

    @Test
    @DisplayName("createProduct - when valid data then call service layer and return 200")
    void createProduct_whenValidData_callServiceLayer_return200() throws Exception {
        ProductRequest productRequest = buildThousandSunnyRequest();
        Product productWithoutId = buildThousandSunny();
        productWithoutId.setId(null);
        productWithoutId.setCategory(null);
        Product productWithIdAndCategory = buildThousandSunny();

        when(productService.createProduct(SHIP_CAT_ID, productWithoutId))
                .thenReturn(productWithIdAndCategory);
        ProductResponse productResponse =
                getMvc().send(POST, PRODUCT_URI)
                        .withBody(productRequest)
                        .withJwt(adminToken(LUFFY_ID))
                        .thenExpectStatus(OK)
                        .getResponseBody(ProductResponse.class);

        assertProductResponse(productResponse);
    }

    @Test
    @DisplayName("createProduct - when unauthenticated user then return FORBIDDEN")
    void createProduct_whenUnauthenticated_return403() throws Exception {
        getMvc().send(POST, PRODUCT_URI).thenExpectStatus(FORBIDDEN);
    }

    @Test
    @DisplayName("updateProduct - when valid data then call service layer and return 200")
    void updateProduct_whenValidData_callServiceLayer_return200() throws Exception {
        ProductRequest productRequest = buildThousandSunnyRequest();
        Product productWithoutCategory = buildThousandSunny();
        productWithoutCategory.setCategory(null);

        Product productWithIdAndCategory = buildThousandSunny();

        when(productService.updateProduct(SHIP_CAT_ID, productWithoutCategory))
                .thenReturn(productWithIdAndCategory);
        ProductResponse productResponse =
                getMvc().send(PUT, PRODUCT_URI_WITH_ID, TSUNNY_PROD_ID)
                        .withBody(productRequest)
                        .withJwt(adminToken(LUFFY_ID))
                        .thenExpectStatus(OK)
                        .getResponseBody(ProductResponse.class);

        assertProductResponse(productResponse);
    }

    @Test
    @DisplayName("updateProduct - when unauthenticated user then return FORBIDDEN")
    void updateProduct_whenUnauthenticated_return403() throws Exception {
        getMvc().send(PUT, PRODUCT_URI, TSUNNY_PROD_ID).thenExpectStatus(FORBIDDEN);
    }

    @Test
    @DisplayName("deleteProduct - when valid data then call service layer and return 200")
    void deleteProduct_whenValidData_callServiceLayer_return200() throws Exception {
        getMvc().send(DELETE, PRODUCT_URI_WITH_ID, TSUNNY_PROD_ID)
                .withJwt(adminToken(LUFFY_ID))
                .thenExpectStatus(OK);

        verify(productService).deleteProduct(TSUNNY_PROD_ID);
    }

    @Test
    @DisplayName("deleteProduct - when unauthenticated user then return FORBIDDEN")
    void deleteProduct_whenUnauthenticated_return403() throws Exception {
        getMvc().send(DELETE, PRODUCT_URI, TSUNNY_PROD_ID).thenExpectStatus(FORBIDDEN);
    }

    @Test
    @DisplayName("getProduct - when admin role then call service getProduct and return 200")
    void getProduct_whenAdminRole_callServiceGetProduct_return200() throws Exception {
        Product product = buildThousandSunny();

        when(productService.getProduct(TSUNNY_PROD_ID)).thenReturn(product);

        ProductResponse productResponse =
                getMvc().send(GET, PRODUCT_URI_WITH_ID, TSUNNY_PROD_ID)
                        .withJwt(adminToken(LUFFY_ID))
                        .thenExpectStatus(OK)
                        .getResponseBody(ProductResponse.class);

        assertProductResponse(productResponse);
    }

    @Test
    @DisplayName(
            "getProduct - when customer role then call service getActiveProduct and return 200")
    void getProduct_whenCustomerRole_callServiceGetActiveProduct_return200() throws Exception {
        Product product = buildThousandSunny();

        when(productService.getActiveProduct(TSUNNY_PROD_ID)).thenReturn(product);

        ProductResponse productResponse =
                getMvc().send(GET, PRODUCT_URI_WITH_ID, TSUNNY_PROD_ID)
                        .withJwt(customerToken(LUFFY_ID))
                        .thenExpectStatus(OK)
                        .getResponseBody(ProductResponse.class);

        assertProductResponse(productResponse);
    }

    @Test
    @DisplayName("getProduct - when unauthenticated user then return FORBIDDEN")
    void getProduct_whenUnauthenticated_return403() throws Exception {
        getMvc().send(GET, PRODUCT_URI, TSUNNY_PROD_ID).thenExpectStatus(FORBIDDEN);
    }

    @Test
    @DisplayName(
            "getProducts - when admin role call service layer passing down all queryParams and return 200")
    void getProducts_whenAdminRole_callServiceLayer_withParams_return200() throws Exception {
        List<Product> products = List.of(buildThousandSunny());
        when(productService.getProducts(
                        SHIP_CAT_ID,
                        TSUNNY_PROD_NAME,
                        TSUNNY_PROD_DESC,
                        BigDecimal.ONE,
                        BigDecimal.TEN,
                        false))
                .thenReturn(products);

        ProductResponse[] productResponse =
                getMvc().send(GET, PRODUCT_URI)
                        .withQueryParam("categoryId", SHIP_CAT_ID)
                        .withQueryParam("name", TSUNNY_PROD_NAME)
                        .withQueryParam("description", TSUNNY_PROD_DESC)
                        .withQueryParam("priceFrom", BigDecimal.ONE.toString())
                        .withQueryParam("priceTo", BigDecimal.TEN.toString())
                        .withQueryParam("isActive", "false")
                        .withJwt(adminToken(LUFFY_ID))
                        .thenExpectStatus(OK)
                        .getResponseBody(ProductResponse[].class);

        assertThat(productResponse.length, is(1));
        assertProductResponse(productResponse[0]);
    }

    @Test
    @DisplayName(
            "getProducts - when all parameters are null then call service layer and return 200")
    void getProducts_whenParametersAreNull_callServiceLayer_return200() throws Exception {
        List<Product> products = List.of(buildThousandSunny());
        when(productService.getProducts(null, null, null, null, null, null)).thenReturn(products);

        ProductResponse[] productResponse =
                getMvc().send(GET, PRODUCT_URI)
                        .withJwt(adminToken(LUFFY_ID))
                        .thenExpectStatus(OK)
                        .getResponseBody(ProductResponse[].class);

        assertThat(productResponse.length, is(1));
        assertProductResponse(productResponse[0]);
    }

    @Test
    @DisplayName(
            "getProducts - when customer role call service layer and override isActive to always be true then return 200")
    void getProducts_whenCustomerRole_callServiceLayer_withIsActiveAsTrue_return200()
            throws Exception {
        List<Product> products = List.of(buildThousandSunny());
        when(productService.getProducts(
                        SHIP_CAT_ID,
                        TSUNNY_PROD_NAME,
                        TSUNNY_PROD_DESC,
                        BigDecimal.ONE,
                        BigDecimal.TEN,
                        true))
                .thenReturn(products);

        ProductResponse[] productResponse =
                getMvc().send(GET, PRODUCT_URI)
                        .withQueryParam("categoryId", SHIP_CAT_ID)
                        .withQueryParam("name", TSUNNY_PROD_NAME)
                        .withQueryParam("description", TSUNNY_PROD_DESC)
                        .withQueryParam("priceFrom", BigDecimal.ONE.toString())
                        .withQueryParam("priceTo", BigDecimal.TEN.toString())
                        .withQueryParam("isActive", "false")
                        .withJwt(customerToken(LUFFY_ID))
                        .thenExpectStatus(OK)
                        .getResponseBody(ProductResponse[].class);

        assertThat(productResponse.length, is(1));
        assertProductResponse(productResponse[0]);
    }

    @Test
    @DisplayName("getProducts - when unauthenticated user then return FORBIDDEN")
    void getProducts_whenUnauthenticated_return403() throws Exception {
        getMvc().send(GET, PRODUCT_URI).thenExpectStatus(FORBIDDEN);
    }

    private void assertProductResponse(ProductResponse productResponse) {
        assertThat(productResponse.getId(), is(TSUNNY_PROD_ID));
        assertThat(productResponse.getCategoryId(), is(SHIP_CAT_ID));
        assertThat(productResponse.getDescription(), is(TSUNNY_PROD_DESC));
        assertThat(productResponse.getImageUrl(), is(TSUNNY_PROD_IMAGEURL));
        assertThat(productResponse.getName(), is(TSUNNY_PROD_NAME));
        assertThat(productResponse.getPrice(), is(TSUNNY_PROD_PRICE));
    }
}
