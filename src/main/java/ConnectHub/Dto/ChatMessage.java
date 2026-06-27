package ConnectHub.Dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    private String senderEmail;

    private String receiverEmail;

    private String content;
}