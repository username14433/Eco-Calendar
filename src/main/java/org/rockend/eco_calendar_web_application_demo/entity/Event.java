package org.rockend.eco_calendar_web_application_demo.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "summary",  nullable = false, length = 150)
    private String summary;

    @Column(name = "description",  nullable = false, length = 300)
    private String description;

    @Column(name = "start_time",  nullable = false, length = 20)
    private String startTime;

    @Column(name = "end_time",  nullable = false, length = 20)
    private String endTime;

    @Column(name = "status",  nullable = false)
    private EventStatus status;


    public Event() { }

    public Event(String summary, String description, String startTime, String endTime, EventStatus status) {
        this.summary = summary;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public Event(String summary, String description, EventStatus status) {
        this.summary = summary;
        this.description = description;
        this.status = status;
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
