package org.rockend.eco_calendar_web_application_demo.entity.dto;

import org.rockend.eco_calendar_web_application_demo.entity.EventStatus;

public class CreateEventRequest {
    private String summary;
    private String description;
    private String startTime; // "2025-11-30T10:00"
    private String endTime;   // "2025-11-30T12:00"
    private EventStatus status;

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

    public EventStatus getStatus() {
        return status;
    }
}