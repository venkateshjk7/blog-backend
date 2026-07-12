package simple.blog.blogsite.controller;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String password;
}
