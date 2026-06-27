package ConnectHub.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private final AuthUtil authUtil;
    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel){
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(
                        message,
                        StompHeaderAccessor.class
                );
        if (StompCommand.CONNECT.equals(
                accessor.getCommand())){
            String authHeader =
                    accessor.getFirstNativeHeader(
                            "Authorization"
                    );
            if(authHeader != null &&
                    authHeader.startsWith("Bearer ")){
                String token =
                        authHeader.substring(7);
                String email =
                        authUtil.verifyToken(token);
                UsernamePasswordAuthenticationToken
                        authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of()
                        );
                accessor.setUser(authentication);
            }
        }
        return message;
    }

}
