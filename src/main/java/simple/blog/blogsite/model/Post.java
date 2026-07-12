package simple.blog.blogsite.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Post {
    @Id
    @GeneratedValue
    Long id;
    private String imageUrl;
    @NotNull
    @NotBlank
    String title;
    @NotNull
    @NotBlank
    String description;
    @NotNull
    @NotBlank
    String email;

}
