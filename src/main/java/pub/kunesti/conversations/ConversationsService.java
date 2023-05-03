package pub.kunesti.conversations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pub.kunesti.event.Event;
import pub.kunesti.event.EventRepository;
import pub.kunesti.model.SimplePage;
import pub.kunesti.util.NotFoundException;


@Service
public class ConversationsService {

    private final ConversationsRepository conversationsRepository;
    private final EventRepository eventRepository;

    public ConversationsService(final ConversationsRepository conversationsRepository,
            final EventRepository eventRepository) {
        this.conversationsRepository = conversationsRepository;
        this.eventRepository = eventRepository;
    }

    public SimplePage<ConversationsDTO> findAll(final String filter, final Pageable pageable) {
        Page<Conversations> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = conversationsRepository.findAllById(longFilter, pageable);
        } else {
            page = conversationsRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map((conversations) -> mapToDTO(conversations, new ConversationsDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public ConversationsDTO get(final Long id) {
        return conversationsRepository.findById(id)
                .map(conversations -> mapToDTO(conversations, new ConversationsDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ConversationsDTO conversationsDTO) {
        final Conversations conversations = new Conversations();
        mapToEntity(conversationsDTO, conversations);
        return conversationsRepository.save(conversations).getId();
    }

    public void update(final Long id, final ConversationsDTO conversationsDTO) {
        final Conversations conversations = conversationsRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(conversationsDTO, conversations);
        conversationsRepository.save(conversations);
    }

    public void delete(final Long id) {
        conversationsRepository.deleteById(id);
    }

    private ConversationsDTO mapToDTO(final Conversations conversations,
            final ConversationsDTO conversationsDTO) {
        conversationsDTO.setId(conversations.getId());
        conversationsDTO.setMessage(conversations.getMessage());
        conversationsDTO.setEvent(conversations.getEvent() == null ? null : conversations.getEvent().getId());
        return conversationsDTO;
    }

    private Conversations mapToEntity(final ConversationsDTO conversationsDTO,
            final Conversations conversations) {
        conversations.setMessage(conversationsDTO.getMessage());
        final Event event = conversationsDTO.getEvent() == null ? null : eventRepository.findById(conversationsDTO.getEvent())
                .orElseThrow(() -> new NotFoundException("event not found"));
        conversations.setEvent(event);
        return conversations;
    }

}
