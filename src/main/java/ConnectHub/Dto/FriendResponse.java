package ConnectHub.Dto;

import lombok.*;

@Builder
@Getter
@Setter
public class FriendResponse {
    private Long id;
    private String username;
    private String email;
}
