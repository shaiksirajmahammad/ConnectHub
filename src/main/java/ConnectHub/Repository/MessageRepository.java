package ConnectHub.Repository;

import ConnectHub.Entity.Message;
import ConnectHub.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Long> {
    @Query("""
SELECT m
FROM Message m
WHERE
    (m.sender = :user1 AND m.receiver = :user2)
 OR (m.sender = :user2 AND m.receiver = :user1)
ORDER BY m.sentAt
""")
    List<Message> findConversation(
            User user1,
            User user2
    );
}
