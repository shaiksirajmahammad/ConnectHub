package ConnectHub.Service;

import ConnectHub.Dto.LoginRequest;
import ConnectHub.Dto.LoginResponse;
import ConnectHub.Dto.RegisterRequest;
import ConnectHub.Entity.User;
import ConnectHub.Repository.UserRepository;
import ConnectHub.Security.AuthUtil;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    public User register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
           throw new RuntimeException("user with email already exist");

        }
        User user= User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode( request.getPassword()))
                .createdAt(LocalDateTime.now())

                .build();
        return userRepository.save(user);



    }

    public LoginResponse login(LoginRequest loginRequest) {
//        User user=userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
        Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
        User user=(User) authentication.getPrincipal();
        return LoginResponse.builder()
                .id(user.getId())
                .jwt(authUtil.generateJWT(user))
                .build();


    }
}
