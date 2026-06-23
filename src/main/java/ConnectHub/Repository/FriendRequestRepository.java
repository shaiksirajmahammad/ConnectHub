package ConnectHub.Repository;

import ConnectHub.Entity.FriendRequest;
import ConnectHub.Entity.FriendRequestStatus;
import ConnectHub.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest,Long> {
    boolean existsBySenderAndReceiverAndStatus(
            User sender,
            User receiver,
            FriendRequestStatus status
    );
    List<FriendRequest>
    findByReceiverAndStatus(
            User receiver,
            FriendRequestStatus status
    );
    FriendRequest findBySender(User user);
    @Query("""
    SELECT f
    FROM FriendRequest f
    WHERE f.status = 'ACCEPTED'
    AND (f.sender = :user OR f.receiver = :user)
""")
    List<FriendRequest> findAcceptedFriends(User user);

    @Query("""
SELECT f
FROM FriendRequest f
WHERE f.status = 'ACCEPTED'
AND (
      (f.sender = :sender AND f.receiver = :receiver)
   OR (f.sender = :receiver AND f.receiver = :sender)
)
""")
    Optional<FriendRequest> findAcceptedFriendship(
            User sender,
            User receiver
    );

}
