package simple.blog.blogsite.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    @Id
    @GeneratedValue
    private Long id;
    private String token;
    private Long userId;
    private Instant expiresAt;
    private boolean used;
}
