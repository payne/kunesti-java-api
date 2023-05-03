package pub.kunesti.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pub.kunesti.conversations.Conversations;
import pub.kunesti.conversations.ConversationsRepository;
import pub.kunesti.model.SimplePage;
import pub.kunesti.users.Users;
import pub.kunesti.users.UsersRepository;
import pub.kunesti.util.NotFoundException;
import pub.kunesti.util.WebUtils;


@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UsersRepository usersRepository;
    private final ConversationsRepository conversationsRepository;

    public EventService(final EventRepository eventRepository,
            final UsersRepository usersRepository,
            final ConversationsRepository conversationsRepository) {
        this.eventRepository = eventRepository;
        this.usersRepository = usersRepository;
        this.conversationsRepository = conversationsRepository;
    }

    public SimplePage<EventDTO> findAll(final String filter, final Pageable pageable) {
        Page<Event> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = eventRepository.findAllById(longFilter, pageable);
        } else {
            page = eventRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map((event) -> mapToDTO(event, new EventDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public EventDTO get(final Long id) {
        return eventRepository.findById(id)
                .map(event -> mapToDTO(event, new EventDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final EventDTO eventDTO) {
        final Event event = new Event();
        mapToEntity(eventDTO, event);
        return eventRepository.save(event).getId();
    }

    public void update(final Long id, final EventDTO eventDTO) {
        final Event event = eventRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(eventDTO, event);
        eventRepository.save(event);
    }

    public void delete(final Long id) {
        eventRepository.deleteById(id);
    }

    private EventDTO mapToDTO(final Event event, final EventDTO eventDTO) {
        eventDTO.setId(event.getId());
        eventDTO.setTitle(event.getTitle());
        eventDTO.setDescription(event.getDescription());
        eventDTO.setStartTime(event.getStartTime());
        eventDTO.setLocation(event.getLocation());
        eventDTO.setRsvpRequired(event.getRsvpRequired());
        return eventDTO;
    }

    private Event mapToEntity(final EventDTO eventDTO, final Event event) {
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setStartTime(eventDTO.getStartTime());
        event.setLocation(eventDTO.getLocation());
        event.setRsvpRequired(eventDTO.getRsvpRequired());
        return event;
    }

    public String getReferencedWarning(final Long id) {
        final Event event = eventRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Users eventsUsers = usersRepository.findFirstByEvents(event);
        if (eventsUsers != null) {
            return WebUtils.getMessage("event.users.events.referenced", eventsUsers.getId());
        }
        final Conversations eventConversations = conversationsRepository.findFirstByEvent(event);
        if (eventConversations != null) {
            return WebUtils.getMessage("event.conversations.event.referenced", eventConversations.getId());
        }
        return null;
    }

}
