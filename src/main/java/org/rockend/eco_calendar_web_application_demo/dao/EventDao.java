package org.rockend.eco_calendar_web_application_demo.dao;

import org.rockend.eco_calendar_web_application_demo.entity.Event;
import org.rockend.eco_calendar_web_application_demo.entity.EventStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class EventDao {

    private final List<Event> events = new ArrayList<>(
            Arrays.asList(
                    new Event("Субботник",
                            "Просто субботник в следующую субботу",
                            LocalDateTime.of(2025, 11, 1, 10, 0, 0, 0).toString(),
                            LocalDateTime.of(2025, 11, 1, 12, 0, 0, 0).toString(),
                            EventStatus.AWAITING
                    ),
                    new Event("День эколога",
                            "Сегодня день эколога!",
                            EventStatus.FINISHED
                    ),
                    new Event("День осведомлённости о пингвинах",
                            "20 января мировая общественность отмечает экологический праздник в честь" +
                                    " одной из самых необычных птиц в мире — День осведомленности о пингвинах" +
                                    " (англ. Penguin Awareness Day).",
                            EventStatus.AWAITING
                    )
            )
    );

    public List<Event> getEvents() {
        return new ArrayList<>(events);
    }

    public void saveEvent(Event event) {
        events.add(event);
    }

    public void removeEvent(long id) {
        events.removeIf(item -> item.getId() == id);
    }

    public Event findEventById(long id) {
        for (Event event : events) {
            if (event.getId() == id) {
                return event;
            }
        }
        return null;
    }


}
