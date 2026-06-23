package ConnectHub.Dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginResponse {
    Long id;
    String jwt;
}
