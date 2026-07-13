package simple.blog.blogsite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import simple.blog.blogsite.service.UserService;
import simple.blog.blogsite.utils.JwtUtil;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() throws Exception{
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

        public SecurityConfig(OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
            this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        }

    @Bean

    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter, ClientRegistrationRepository clientRegistrationRepository) throws Exception{

        DefaultOAuth2AuthorizationRequestResolver resolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");

        resolver.setAuthorizationRequestCustomizer(customizer ->
                customizer.additionalParameters(params -> params.put("prompt", "login"))
        );

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))

                //.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/","/login.html","/index.html","/auth/login","/auth/register","/home.html","/auth/forgot-password","/auth/verify-otp","/auth/reset-password","/api/v1/posts/create","/oauth2/**","/auth/**","/uploads/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        //.defaultSuccessUrl("http://localhost:5500/index.html", true)// 👈 force login.html
                       //.failureUrl("http://localhost:5500/home.html")
                        .failureUrl("https://blog-frontend-pi-pearl.vercel.app/home.html")
                        .successHandler(oAuth2LoginSuccessHandler)   // ✅ use custom handler
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        // Restrict origins when credentials are allowed; adjust to match your frontend origin(s)
        //configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedOrigins(List.of("http://localhost:5500", "https://blog-frontend-pi-pearl.vercel.app"));
        // include OPTIONS to allow CORS preflight from browsers
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));
        configuration.setAllowCredentials(false);


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);

        return source;
    }
}


