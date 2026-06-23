package ConnectHub.Controller;

import ConnectHub.Dto.Profile;
import ConnectHub.Entity.FriendRequest;
import ConnectHub.Entity.User;
import ConnectHub.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    @GetMapping("/profile")
    public ResponseEntity<Profile> profile(){
        User user= (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Profile profile=Profile.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(profile);
    }


}
