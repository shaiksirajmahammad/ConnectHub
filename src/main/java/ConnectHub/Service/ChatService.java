

package ConnectHub.Service;

import ConnectHub.Dto.ChatMessage;
import ConnectHub.Entity.FriendRequest;
import ConnectHub.Entity.Message;
import ConnectHub.Entity.User;
import ConnectHub.Repository.FriendRequestRepository;
import ConnectHub.Repository.MessageRepository;
import ConnectHub.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendPrivateMessage(
            ChatMessage chatMessage,
            String senderEmail) {

        User sender =
                userRepository
                        .findByEmail(senderEmail)
                        .orElseThrow();

        User receiver =
                userRepository
                        .findByEmail(
                                chatMessage.getReceiverEmail()
                        )
                        .orElseThrow();

        friendRequestRepository
                .findAcceptedFriendship(
                        sender,
                        receiver
                )
                .orElseThrow();

        Message message =
                Message.builder()
                        .sender(sender)
                        .receiver(receiver)
                        .content(
                                chatMessage.getContent()
                        )
                        .sentAt(
                                LocalDateTime.now()
                        )
                        .build();

        messageRepository.save(message);

        messagingTemplate
                .convertAndSendToUser(
                        receiver.getEmail(),
                        "/queue/messages",
                        chatMessage
                );
    }
}