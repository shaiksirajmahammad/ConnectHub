package ConnectHub.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne()
    @JoinColumn(name = "sender_id")
    private User sender;
    @ManyToOne()
    @JoinColumn(name="receiver_id")
    private User receiver;
    @Enumerated(EnumType.STRING)
    private FriendRequestStatus status;
    private LocalDateTime createdAt;
}
