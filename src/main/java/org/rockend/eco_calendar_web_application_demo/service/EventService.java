package org.rockend.eco_calendar_web_application_demo.service;

import org.rockend.eco_calendar_web_application_demo.dao.EventDao;
import org.rockend.eco_calendar_web_application_demo.entity.Event;
import org.rockend.eco_calendar_web_application_demo.entity.EventStatus;
import org.rockend.eco_calendar_web_application_demo.entity.dto.EventDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    private EventDao eventDao;

    public EventService(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    public EventDto findAllEvents(){
        List<Event> events = eventDao.getEvents();
        int upcomingEventsQuantity = (int)events.stream()
                .filter(event -> event.getStatus() == EventStatus.AWAITING).count();
        int finishedEventsQuantity = (int)events.stream()
                .filter(event -> event.getStatus() == EventStatus.FINISHED).count();

        return new EventDto(events, upcomingEventsQuantity, finishedEventsQuantity);
    }

    public Event findEventById(long id) {
        return eventDao.findEventById(id);
    }

    public String formatDateTime(String dateTime) {
        String[] dateTimeElems = dateTime.split("T");
        String[] dateElems = dateTimeElems[0].split("-");

        return  dateElems[2] + "-" + dateElems[1] + "-" + dateElems[0] + "\n" + dateTimeElems[1];
    }

    public void addEvent(String summary, String description, String start, String end, EventStatus status) {
        eventDao.saveEvent(new Event(summary, description, start, end, status));
    }

    public void deleteEvent(long id) {
        eventDao.removeEvent(id);
    }
}
