package org.rockend.eco_calendar_web_application_demo.entity;

import java.time.LocalDateTime;

public class Event {
    private final String summary;
    private final String description;
    private String startTime;
    private String endTime;
    private final EventStatus status;
    private final long id;
    private static int counterSequence = 1;

    public Event(String summary, String description, String startTime, String endTime, EventStatus status) {
        this.summary = summary;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.id = counterSequence++;
    }

    public Event(String summary, String description, EventStatus status) {
        this.summary = summary;
        this.description = description;
        this.status = status;
        this.id = counterSequence++;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public long getId() {
        return id;
    }

    public EventStatus getStatus() {
        return status;
    }
}
