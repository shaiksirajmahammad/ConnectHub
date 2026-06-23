package ConnectHub.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class MessageResponse {

    private String sender;
    private String content;
    private LocalDateTime sentAt;
}
