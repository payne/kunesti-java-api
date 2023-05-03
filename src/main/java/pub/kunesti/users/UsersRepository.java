package pub.kunesti.users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pub.kunesti.event.Event;


public interface UsersRepository extends JpaRepository<Users, Long> {

    Page<Users> findAllById(Long id, Pageable pageable);

    boolean existsByUserNameIgnoreCase(String userName);

    boolean existsByEmailIgnoreCase(String email);

    Users findFirstByEvents(Event event);

}
