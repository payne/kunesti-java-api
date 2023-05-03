package pub.kunesti.event;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EventDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String title;

    private String description;

    @NotNull
    private LocalDateTime startTime;

    @Size(max = 255)
    private String location;

    @NotNull
    private Boolean rsvpRequired;

}
