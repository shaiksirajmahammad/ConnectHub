package ConnectHub.Controller;

import ConnectHub.Dto.FriendResponse;
import ConnectHub.Dto.PendingRequestResponse;
import ConnectHub.Dto.RegisterRequest;
import ConnectHub.Entity.User;
import ConnectHub.Service.FriendRequestService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendRequestController {
    private final FriendRequestService friendRequestService;
    @PostMapping("/request/{id}")
    public ResponseEntity<String>request(@PathVariable Long id){
        String status=friendRequestService.request(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(status);

    }
    @PostMapping("/accept/{id}")
    public ResponseEntity<String>AcceptFriendRequest(@PathVariable Long id){
        String status=friendRequestService.accept(id);
        return ResponseEntity.status(HttpStatus.OK).body(status);
    }


    @PostMapping("/reject/{id}")
    public ResponseEntity<String>RejectFriendRequest(@PathVariable Long id){
        String status=friendRequestService.reject(id);
        return ResponseEntity.status(HttpStatus.OK).body(status);
    }
    @GetMapping("/pending")
    public ResponseEntity<List<PendingRequestResponse>>findRequests(){
        List<PendingRequestResponse>userList=friendRequestService.getPendingList();
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }
    @GetMapping("/friendlist")
    public ResponseEntity<List<FriendResponse>>getFriendList(){
        List<FriendResponse>friendResponseList=friendRequestService.getFriendList();
        return ResponseEntity.status(HttpStatus.OK).body(friendResponseList);
    }
}
