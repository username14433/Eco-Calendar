package org.rockend.eco_calendar_web_application_demo.controller;

import org.rockend.eco_calendar_web_application_demo.entity.dto.CreateEventRequest;
import org.rockend.eco_calendar_web_application_demo.entity.dto.EventDto;
import org.rockend.eco_calendar_web_application_demo.service.EventService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventRestController {

    private final EventService eventService;

    public EventRestController(EventService eventService) {
        this.eventService = eventService;
    }

    // 1) Получить все события + статистику (доступно всем)
    @GetMapping
    public EventDto getEvents() {
        return eventService.findAllEvents();
    }

    // 2) Создать новое событие (только ADMIN - через SecurityConfig)
    @PostMapping
    public void createEvent(@RequestBody CreateEventRequest request) {
        eventService.addEvent(
                request.getSummary(),
                request.getDescription(),
                request.getStartTime(),
                request.getEndTime(),
                request.getStatus()
        );
    }

    // 3) Удалить событие (только ADMIN - через SecurityConfig)
    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable int id) {
        eventService.deleteEvent(id);
    }
}
