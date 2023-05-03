package pub.kunesti.users;

import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pub.kunesti.event.Event;
import pub.kunesti.event.EventRepository;
import pub.kunesti.model.SimplePage;
import pub.kunesti.util.NotFoundException;


@Transactional
@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final EventRepository eventRepository;

    public UsersService(final UsersRepository usersRepository,
            final EventRepository eventRepository) {
        this.usersRepository = usersRepository;
        this.eventRepository = eventRepository;
    }

    public SimplePage<UsersDTO> findAll(final String filter, final Pageable pageable) {
        Page<Users> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = usersRepository.findAllById(longFilter, pageable);
        } else {
            page = usersRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map((users) -> mapToDTO(users, new UsersDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public UsersDTO get(final Long id) {
        return usersRepository.findById(id)
                .map(users -> mapToDTO(users, new UsersDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final UsersDTO usersDTO) {
        final Users users = new Users();
        mapToEntity(usersDTO, users);
        return usersRepository.save(users).getId();
    }

    public void update(final Long id, final UsersDTO usersDTO) {
        final Users users = usersRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(usersDTO, users);
        usersRepository.save(users);
    }

    public void delete(final Long id) {
        usersRepository.deleteById(id);
    }

    private UsersDTO mapToDTO(final Users users, final UsersDTO usersDTO) {
        usersDTO.setId(users.getId());
        usersDTO.setUserName(users.getUserName());
        usersDTO.setEmail(users.getEmail());
        usersDTO.setEvents(users.getEvents() == null ? null : users.getEvents().stream()
                .map(event -> event.getId())
                .toList());
        return usersDTO;
    }

    private Users mapToEntity(final UsersDTO usersDTO, final Users users) {
        users.setUserName(usersDTO.getUserName());
        users.setEmail(usersDTO.getEmail());
        final List<Event> events = eventRepository.findAllById(
                usersDTO.getEvents() == null ? Collections.emptyList() : usersDTO.getEvents());
        if (events.size() != (usersDTO.getEvents() == null ? 0 : usersDTO.getEvents().size())) {
            throw new NotFoundException("one of events not found");
        }
        users.setEvents(events.stream().collect(Collectors.toSet()));
        return users;
    }

    public boolean userNameExists(final String userName) {
        return usersRepository.existsByUserNameIgnoreCase(userName);
    }

    public boolean emailExists(final String email) {
        return usersRepository.existsByEmailIgnoreCase(email);
    }

}
