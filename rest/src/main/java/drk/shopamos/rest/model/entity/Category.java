package drk.shopamos.rest.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Category {
    @Id
    @Column(length = 4)
    private String id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String iconUrl;
}
