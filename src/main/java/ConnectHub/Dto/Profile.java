package ConnectHub.Dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Profile {
    String username;
    String email;
    LocalDateTime createdAt;
}
