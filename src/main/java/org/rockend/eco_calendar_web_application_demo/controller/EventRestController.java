package org.rockend.eco_calendar_web_application_demo.controller;

import org.rockend.eco_calendar_web_application_demo.entity.Event;
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

    // 1) Получить все события + статистику (как для главной страницы)
    @GetMapping
    public EventDto getEvents() {
        // тут нужно вызвать тот метод EventService,
        // который использует CommonController для /calendar
        // условно назовём его getEventsDto()
        return eventService.findAllEvents();
    }

    // 2) Создать новое событие
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

    // 3) Удалить событие
    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable int id) {
        eventService.deleteEvent(id);
    }
}
