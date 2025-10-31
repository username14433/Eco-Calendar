package org.rockend.eco_calendar_web_application_demo.entity.dto;

import org.rockend.eco_calendar_web_application_demo.entity.Event;

import java.util.List;

public class EventDto {
    private List<Event> events;
    private int upcomingEventsQuantity;
    private int finishedEventsQuantity;


    public EventDto(List<Event> events, int upcomingEventsQuantity, int finishedEventsQuantity) {
        this.events = events;
        this.upcomingEventsQuantity = upcomingEventsQuantity;
        this.finishedEventsQuantity = finishedEventsQuantity;
    }

    public List<Event> getEvents() {
        return events;
    }

    public int getUpcomingEventsQuantity() {
        return upcomingEventsQuantity;
    }

    public int getFinishedEventsQuantity() {
        return finishedEventsQuantity;
    }
}
