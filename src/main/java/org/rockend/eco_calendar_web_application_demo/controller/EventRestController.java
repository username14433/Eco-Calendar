package org.rockend.eco_calendar_web_application_demo.controller;

import org.rockend.eco_calendar_web_application_demo.entity.dto.CreateEventRequest;
import org.rockend.eco_calendar_web_application_demo.entity.dto.EventDto;
import org.rockend.eco_calendar_web_application_demo.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CookieValue;

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

    // 2) Создать новое событие (ТОЛЬКО АДМИН)
    @PostMapping
    public ResponseEntity<?> createEvent(
            @RequestBody CreateEventRequest request,
            @CookieValue(value = "isAdmin", defaultValue = "false") String isAdminCookie
    ) {
        if (!"true".equals(isAdminCookie)) {
            // обычный пользователь не может создавать события
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        eventService.addEvent(
                request.getSummary(),
                request.getDescription(),
                request.getStartTime(),
                request.getEndTime(),
                request.getStatus()
        );

        return ResponseEntity.ok().build();
    }

    // 3) Удалить событие (ТОЛЬКО АДМИН)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(
            @PathVariable int id,
            @CookieValue(value = "isAdmin", defaultValue = "false") String isAdminCookie
    ) {
        if (!"true".equals(isAdminCookie)) {
            // обычный пользователь не может удалять события
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
