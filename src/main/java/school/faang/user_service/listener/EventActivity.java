package school.faang.user_service.listener;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;

@Component
public class EventActivity implements Activity{

    private final Long rating = 5L;
    @Override
    public Long getUserId(Object object) {
        Event event = (Event) object;
        return event.getOwner().getId();
    }

    @Override
    public Long getRating(Object object) {
        return rating;
    }

    @Override
    public Class getEntityClass() {
        return Event.class;
    }
}