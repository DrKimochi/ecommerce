package drk.shopamos.rest.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Integer id;

    private String categoryId;

    private String name;

    private String description;

    private BigDecimal price;

    private String imageUrl;

    private boolean active;
}
