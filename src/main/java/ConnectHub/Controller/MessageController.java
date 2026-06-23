package ConnectHub.Controller;

import ConnectHub.Dto.MessageResponse;
import ConnectHub.Dto.SendMessage;
import ConnectHub.Service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    @PostMapping("send/{id}")
    public ResponseEntity<String> sendMessage(@PathVariable Long id, @RequestBody SendMessage sendMessage)
    {
        String response=messageService.sendMessage(id,sendMessage);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("history/{id}")
    public ResponseEntity<List<MessageResponse>>viewHistory(@PathVariable Long id){
        List<MessageResponse>messageResponses=messageService.getHistory(id);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponses);

    }
}
