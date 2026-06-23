package ConnectHub.Dto;

import lombok.*;

@Builder
@Getter
@Setter
public class PendingRequestResponse {
    private Long id;
    private String username;
}
