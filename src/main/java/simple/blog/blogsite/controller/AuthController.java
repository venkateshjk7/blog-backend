package simple.blog.blogsite.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import simple.blog.blogsite.model.User;
import simple.blog.blogsite.repository.UserRepository;
import simple.blog.blogsite.service.SmsService;
import simple.blog.blogsite.service.UserService;
import simple.blog.blogsite.utils.JwtUtil;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtutil;
    private final SmsService smsService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Map<String, String> body){
        String email = body.get("email");
        String password = body.get("password");
        //String cpassword=body.get("cpassword");
        String mobileNumber=body.get("phone");

        password = passwordEncoder.encode(password);

        if(userRepository.findByEmail(email).isPresent()){
            return new ResponseEntity<>("Email already exists",HttpStatus.CONFLICT);
        }
        userService.createUser(User.builder().email(email).password(password).mobileNumber(mobileNumber).build());
        return new ResponseEntity<>("Successfully Registered", HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> body){
        String email = body.get("email");
        String password = body.get("password");

        var userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            return new ResponseEntity<>("User not Registered", HttpStatus.UNAUTHORIZED);
        }
        User user = userOptional.get();
        if(!passwordEncoder.matches(password, user.getPassword())){
            return new ResponseEntity<>("Invalid User", HttpStatus.UNAUTHORIZED);
        }
        String token = jwtutil.generateToken(email);
        return ResponseEntity.ok(Map.of("token",token));
    }

        @PostMapping("/forgot-password")
        public ResponseEntity<?> forgotPassword(
        @RequestParam String email) {


        Optional<User> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Email not found");
        }

        User user = optionalUser.get();

        String mobile =
            user.getMobileNumber();

        String maskedMobile =
            "******" +
                    mobile.substring(
                            mobile.length()-4);

        return ResponseEntity.ok(maskedMobile);
        }

    @PostMapping("/send-otp")
    public String sendOtp(
            @RequestParam String email) {

        Optional<User> optionalUser =
                userRepository.findByEmail(email);

        String otp =
                String.valueOf(
                        (int)(Math.random()*900000)
                                +100000);

        User user = optionalUser.get();

        user.setOtp(otp);

        userRepository.save(user);

        smsService.sendOtp(
                user.getMobileNumber(),
              otp);

        return "OTP Sent";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestBody OtpRequest request){

        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if(optionalUser.isEmpty()) {
            return "User Not Found";
        }

        User user = optionalUser.get();

        if(!user.getOtp().equals(request.getOtp())) {
            return "Invalid OTP";
        }

        return "OTP Verified";
    }

    @PutMapping("/reset-password")
    public String resetPassword(
            @RequestBody ResetPasswordRequest request){

        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if(optionalUser.isEmpty()) {
            return "User Not Found";
        }

        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setOtp(null);
        userRepository.save(user);

        return "Password Updated";
    }
}

