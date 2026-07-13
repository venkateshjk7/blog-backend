package simple.blog.blogsite;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import simple.blog.blogsite.utils.JwtUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;

    public OAuth2LoginSuccessHandler(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Authentication authentication) throws IOException {
        Object principal = authentication.getPrincipal();

        String email;
        if (principal instanceof CustomOAuth2User customUser) {
            email = customUser.getEmail();
        } else if (principal instanceof DefaultOidcUser oidcUser) {
            email = oidcUser.getAttribute("email");
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }

        String token = jwtUtil.generateToken(email);
        if (token.isEmpty()) {
            // If user came from register page → send them to login.html
            //response.sendRedirect("http://localhost:5500/login.html");
            response.sendRedirect("https://blog-frontend-pi-pearl.vercel.app/login.html");
        }
        else {
           //String redirectUrl = "http://localhost:5500/home.html?email=" +
            String redirectUrl = "https://blog-frontend-pi-pearl.vercel.app/home.html?email=" +
                    URLEncoder.encode(email, StandardCharsets.UTF_8) +
                    "&token=" +
                    URLEncoder.encode(token, StandardCharsets.UTF_8);

            response.sendRedirect(redirectUrl);
        }
    }
}












