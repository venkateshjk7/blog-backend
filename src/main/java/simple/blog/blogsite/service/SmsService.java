package simple.blog.blogsite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import simple.blog.blogsite.repository.UserRepository;

@Service
public class SmsService {

    @Autowired
    private UserRepository userRepository;
    public void sendOtp(String mobileNumber,String otp){
            System.out.println("Sending OTP "+otp+" to "+mobileNumber);
    }
}
