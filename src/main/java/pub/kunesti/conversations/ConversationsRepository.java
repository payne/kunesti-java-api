package pub.kunesti.conversations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pub.kunesti.event.Event;


public interface ConversationsRepository extends JpaRepository<Conversations, Long> {

    Page<Conversations> findAllById(Long id, Pageable pageable);

    Conversations findFirstByEvent(Event event);

}
