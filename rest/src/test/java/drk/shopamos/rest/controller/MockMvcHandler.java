package drk.shopamos.rest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.UnsupportedEncodingException;

public class MockMvcHandler {

    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;

    MockMvcHandler(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public MockMvcRequestBuilderHandler send(HttpMethod httpMethod, String uri) {
        return new MockMvcRequestBuilderHandler(httpMethod, uri);
    }

    public class MockMvcRequestBuilderHandler {
        private MockHttpServletRequestBuilder mockHttpServletRequestBuilder;

        private MockMvcRequestBuilderHandler(HttpMethod httpMethod, String uri) {
            this.mockHttpServletRequestBuilder =
                    request(httpMethod, uri).contentType(MediaType.APPLICATION_JSON);
        }

        public MockMvcRequestBuilderHandler withJwt(String token) {
            this.mockHttpServletRequestBuilder =
                    mockHttpServletRequestBuilder.header("authorization", "Bearer " + token);
            return this;
        }

        MockMvcRequestBuilderHandler withBody(Object body) throws JsonProcessingException {
            this.mockHttpServletRequestBuilder =
                    mockHttpServletRequestBuilder.content(objectMapper.writeValueAsString(body));
            return this;
        }

        public MockMvcResultHandler thenExpectStatus(HttpStatus status) throws Exception {
            return new MockMvcResultHandler(
                    mockMvc.perform(this.mockHttpServletRequestBuilder)
                            .andExpect(status().is(status.value())));
        }

        public class MockMvcResultHandler {
            private final ResultActions resultActions;

            private MockMvcResultHandler(ResultActions resultActions) {
                this.resultActions = resultActions;
            }

            public <T> T getResponseBody(Class<T> bodyType)
                    throws UnsupportedEncodingException, JsonProcessingException {
                return objectMapper.readValue(
                        resultActions.andReturn().getResponse().getContentAsString(), bodyType);
            }
        }
    }
}
