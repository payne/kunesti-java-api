package pub.kunesti.conversations;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ConversationsDTO {

    private Long id;

    @NotNull
    private String message;

    @NotNull
    private Long event;

}
