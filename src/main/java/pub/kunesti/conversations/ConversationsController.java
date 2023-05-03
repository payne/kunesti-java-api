package pub.kunesti.conversations;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pub.kunesti.event.Event;
import pub.kunesti.event.EventRepository;
import pub.kunesti.model.SimplePage;
import pub.kunesti.util.CustomCollectors;
import pub.kunesti.util.WebUtils;


@Controller
@RequestMapping("/conversationss")
public class ConversationsController {

    private final ConversationsService conversationsService;
    private final EventRepository eventRepository;

    public ConversationsController(final ConversationsService conversationsService,
            final EventRepository eventRepository) {
        this.conversationsService = conversationsService;
        this.eventRepository = eventRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("eventValues", eventRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Event::getId, Event::getTitle)));
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<ConversationsDTO> conversationss = conversationsService.findAll(filter, pageable);
        model.addAttribute("conversationss", conversationss);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(conversationss));
        return "conversations/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("conversations") final ConversationsDTO conversationsDTO) {
        return "conversations/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("conversations") @Valid final ConversationsDTO conversationsDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "conversations/add";
        }
        conversationsService.create(conversationsDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("conversations.create.success"));
        return "redirect:/conversationss";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("conversations", conversationsService.get(id));
        return "conversations/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
            @ModelAttribute("conversations") @Valid final ConversationsDTO conversationsDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "conversations/edit";
        }
        conversationsService.update(id, conversationsDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("conversations.update.success"));
        return "redirect:/conversationss";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id, final RedirectAttributes redirectAttributes) {
        conversationsService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("conversations.delete.success"));
        return "redirect:/conversationss";
    }

}
