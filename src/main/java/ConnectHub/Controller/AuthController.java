package ConnectHub.Controller;

import ConnectHub.Dto.LoginRequest;
import ConnectHub.Dto.LoginResponse;
import ConnectHub.Dto.RegisterRequest;
import ConnectHub.Dto.RegisterResponse;
import ConnectHub.Entity.User;
import ConnectHub.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity< RegisterResponse > register(@RequestBody RegisterRequest request){
        User user=userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body( RegisterResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build());

    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        LoginResponse loginResponse=userService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

}
