package ConnectHub.Controller;

import ConnectHub.Dto.ChatMessage;
import ConnectHub.Service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/send")
    public void send(
            ChatMessage message,
            Principal principal) {

        chatService.sendPrivateMessage(
                message,
                principal.getName()
        );
    }
}