package ConnectHub.Service;

import ConnectHub.Dto.MessageResponse;
import ConnectHub.Dto.SendMessage;
import ConnectHub.Entity.FriendRequest;
import ConnectHub.Entity.Message;
import ConnectHub.Entity.User;
import ConnectHub.Repository.FriendRequestRepository;
import ConnectHub.Repository.MessageRepository;
import ConnectHub.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final FriendRequestRepository friendRequestRepository;
    public String sendMessage(Long id, SendMessage sendMessage) {
        User sender=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User receiver=userRepository.findById(id).orElseThrow();
        friendRequestRepository.findAcceptedFriendship(sender,receiver).orElseThrow();
        if(sender.getId().equals(receiver.getId())){
            throw new RuntimeException(
                    "You cannot message yourself"
            );
        }
        Message message=Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(sendMessage.getContent())
                .sentAt(LocalDateTime.now())
                .build();
        messageRepository.save(message);
        return "messages sent successful"+LocalDateTime.now().toString();


    }

    public List<MessageResponse> getHistory(Long id) {
        User loggedInUser=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User otherUser=userRepository.findById(id).orElseThrow();
        friendRequestRepository.findAcceptedFriendship(loggedInUser,otherUser).orElseThrow();
        List<Message>messageList=messageRepository.getChat(loggedInUser.getId(),otherUser.getId());
        List<MessageResponse>messageResponseList=new ArrayList<>();
        for(Message message:messageList){
            messageResponseList.add(MessageResponse.builder()
                            .sender(message.getSender().getUsername())
                            .content(message.getContent())
                            .sentAt(message.getSentAt())
                    .build());
        }
        return messageResponseList;
    }
}
