package pub.kunesti.users;

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
@RequestMapping("/userss")
public class UsersController {

    private final UsersService usersService;
    private final EventRepository eventRepository;

    public UsersController(final UsersService usersService, final EventRepository eventRepository) {
        this.usersService = usersService;
        this.eventRepository = eventRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("eventsValues", eventRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Event::getId, Event::getTitle)));
    }

    @GetMapping
    public String list(@RequestParam(required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<UsersDTO> userss = usersService.findAll(filter, pageable);
        model.addAttribute("userss", userss);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(userss));
        return "users/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("users") final UsersDTO usersDTO) {
        return "users/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("users") @Valid final UsersDTO usersDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasFieldErrors("userName") && usersService.userNameExists(usersDTO.getUserName())) {
            bindingResult.rejectValue("userName", "Exists.users.userName");
        }
        if (!bindingResult.hasFieldErrors("email") && usersService.emailExists(usersDTO.getEmail())) {
            bindingResult.rejectValue("email", "Exists.users.email");
        }
        if (bindingResult.hasErrors()) {
            return "users/add";
        }
        usersService.create(usersDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("users.create.success"));
        return "redirect:/userss";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable final Long id, final Model model) {
        model.addAttribute("users", usersService.get(id));
        return "users/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable final Long id,
            @ModelAttribute("users") @Valid final UsersDTO usersDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        final UsersDTO currentUsersDTO = usersService.get(id);
        if (!bindingResult.hasFieldErrors("userName") &&
                !usersDTO.getUserName().equalsIgnoreCase(currentUsersDTO.getUserName()) &&
                usersService.userNameExists(usersDTO.getUserName())) {
            bindingResult.rejectValue("userName", "Exists.users.userName");
        }
        if (!bindingResult.hasFieldErrors("email") &&
                !usersDTO.getEmail().equalsIgnoreCase(currentUsersDTO.getEmail()) &&
                usersService.emailExists(usersDTO.getEmail())) {
            bindingResult.rejectValue("email", "Exists.users.email");
        }
        if (bindingResult.hasErrors()) {
            return "users/edit";
        }
        usersService.update(id, usersDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("users.update.success"));
        return "redirect:/userss";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable final Long id, final RedirectAttributes redirectAttributes) {
        usersService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("users.delete.success"));
        return "redirect:/userss";
    }

}
