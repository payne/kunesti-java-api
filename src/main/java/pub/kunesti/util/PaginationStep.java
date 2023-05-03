package pub.kunesti.util;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PaginationStep {

    private boolean active = false;
    private boolean disabled = false;
    private String label;
    private String url;

}
