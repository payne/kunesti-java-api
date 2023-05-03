package pub.kunesti.conversations;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.kunesti.model.SimplePage;


@RestController
@RequestMapping(value = "/api/conversationss", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConversationsResource {

    private final ConversationsService conversationsService;

    public ConversationsResource(final ConversationsService conversationsService) {
        this.conversationsService = conversationsService;
    }

    @Operation(
            parameters = {
                    @Parameter(
                            name = "page",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = Integer.class)
                    ),
                    @Parameter(
                            name = "size",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = Integer.class)
                    ),
                    @Parameter(
                            name = "sort",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = String.class)
                    )
            }
    )
    @GetMapping
    public ResponseEntity<SimplePage<ConversationsDTO>> getAllConversationss(
            @RequestParam(required = false, name = "filter") final String filter,
            @Parameter(hidden = true) @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable) {
        return ResponseEntity.ok(conversationsService.findAll(filter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationsDTO> getConversations(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(conversationsService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createConversations(
            @RequestBody @Valid final ConversationsDTO conversationsDTO) {
        final Long createdId = conversationsService.create(conversationsDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateConversations(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ConversationsDTO conversationsDTO) {
        conversationsService.update(id, conversationsDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteConversations(@PathVariable(name = "id") final Long id) {
        conversationsService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
