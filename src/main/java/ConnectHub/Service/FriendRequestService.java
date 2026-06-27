package ConnectHub.Service;

import ConnectHub.Dto.FriendResponse;
import ConnectHub.Dto.PendingRequestResponse;
import ConnectHub.Entity.FriendRequest;
import ConnectHub.Entity.FriendRequestStatus;
import ConnectHub.Entity.User;
import ConnectHub.Repository.FriendRequestRepository;
import ConnectHub.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendRequestService {
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    public String request(Long id) {
        User sender= (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User receiver=userRepository.findById(id).orElseThrow(() ->
                new RuntimeException("User not found"));
        if(sender.getId().equals(receiver.getId())){
            throw new RuntimeException(
                    "Cannot send friend request to yourself"
            );
        }
        if(friendRequestRepository
                .existsBySenderAndReceiverAndStatus(
                        sender,
                        receiver,
                        FriendRequestStatus.PENDING
                )) {

            throw new RuntimeException(
                    "Friend request already sent"
            );
        }
        if(friendRequestRepository
                .existsBySenderAndReceiverAndStatus(
                        receiver,
                        sender,
                        FriendRequestStatus.PENDING
                )) {

            throw new RuntimeException(
                    "User has already sent you a request"
            );
        }
        FriendRequest friendRequest=FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .createdAt(LocalDateTime.now())
                .status(FriendRequestStatus.PENDING)
                .build();
        friendRequestRepository.save(friendRequest);
        return friendRequest.getStatus().toString();

    }
    @Transactional
    public String accept(Long id) {
        User loggedInUser =
                (User) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        FriendRequest friendRequest =
                friendRequestRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Friend request not found"
                                ));

        if(!friendRequest.getReceiver()
                .getId()
                .equals(loggedInUser.getId())) {

            throw new RuntimeException(
                    "You cannot accept this request"
            );
        }

        if(friendRequest.getStatus()!=FriendRequestStatus.PENDING){
            throw new RuntimeException(
                    "Friend request already processed"
            );

        }
        friendRequest.setStatus(FriendRequestStatus.ACCEPTED);
        return friendRequest.getStatus().toString();


    }
    @Transactional
    public String reject(Long id) {
        User loggedInUser =
                (User) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        FriendRequest friendRequest =
                friendRequestRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Friend request not found"
                                ));

        if(!friendRequest.getReceiver()
                .getId()
                .equals(loggedInUser.getId())) {

            throw new RuntimeException(
                    "You cannot reject this request"
            );
        }

        if(friendRequest.getStatus()!=FriendRequestStatus.PENDING){
            throw new RuntimeException(
                    "Friend request already processed"
            );

        }
        friendRequest.setStatus(FriendRequestStatus.REJECTED);
        return friendRequest.getStatus().toString();
    }

    public List<PendingRequestResponse> getPendingList() {
        User loggedInUser=(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<FriendRequest>friendRequestList=friendRequestRepository.findByReceiverAndStatus(loggedInUser,FriendRequestStatus.PENDING);
        List<PendingRequestResponse>userList=new ArrayList<>();
        for(FriendRequest friendRequest:friendRequestList){
            userList.add(PendingRequestResponse.builder()
                            .id(friendRequest.getSender().getId())
                            .username(friendRequest.getSender().getUsername())
                    .build());
        }
        return userList;
    }

    public List<FriendResponse> getFriendList() {
        User loggedInUser=(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<FriendRequest>friendRequestList=friendRequestRepository.findAcceptedFriends(loggedInUser);
        List<FriendResponse>userList=new ArrayList<>();
        for(FriendRequest friendRequest:friendRequestList){
            User friend;
            if(friendRequest.getSender()
                    .getId()
                    .equals(loggedInUser.getId())) {

                friend = friendRequest.getReceiver();

            } else {

                friend = friendRequest.getSender();
            }

            userList.add(
                    FriendResponse.builder()
                            .id(friend.getId())
                            .username(friend.getUsername())
                            .email(friend.getEmail())
                            .build()
            );
        }
        return userList;
    }
}
