package org.rockend.eco_calendar_web_application_demo.controller;

import org.rockend.eco_calendar_web_application_demo.entity.Event;
import org.rockend.eco_calendar_web_application_demo.entity.EventStatus;
import org.rockend.eco_calendar_web_application_demo.entity.dto.EventDto;
import org.rockend.eco_calendar_web_application_demo.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CommonController {

    private final EventService eventService;

    @Autowired
    public CommonController(EventService eventService) {
        this.eventService = eventService;
    }

    @ModelAttribute("events")
    public List<Event> loadEvents() {
        return eventService.findAllEvents().getEvents();
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/calendar";
    }

    @GetMapping("/calendar")
    public String homePage(Model model) {
        EventDto eventDto = eventService.findAllEvents();
        model.addAttribute("upcomingEventsQuantity", eventDto.getUpcomingEventsQuantity());
        model.addAttribute("finishedEventsQuantity", eventDto.getFinishedEventsQuantity());
        return "main-page";
    }

    // форма добавления событий (ДЛЯ АДМИНА, но защита теперь через SecurityConfig)
    @PostMapping("/add")
    public String addEvent(
            @RequestParam String summary,
            @RequestParam String description,
            @RequestParam String startDateTime,
            @RequestParam String endDateTime
    ) {
        String formattedStartDateTime = eventService.formatDateTime(startDateTime);
        String formattedEndDateTime = eventService.formatDateTime(endDateTime);
        eventService.addEvent(summary, description, formattedStartDateTime, formattedEndDateTime, EventStatus.AWAITING);

        // админ остаётся в админ-панели
        return "redirect:/admin-panel";
    }

    // Страница логина админа (только GET — форма, POST обрабатывает Spring Security)
    @GetMapping("/login-to-admin")
    public String loginToAdminPanel() {
        return "login-to-admin";
    }

    // Страница админ-панели — доступна только после логина (ROLE_ADMIN)
    @GetMapping("/admin-panel")
    public String getAdminPanel() {
        return "admin-panel";
    }

    @PostMapping("/delete-event")
    public String deleteEvent(@RequestParam("id") int eventId) {
        eventService.deleteEvent(eventId);
        return "redirect:/admin-panel";
    }

    @GetMapping("/eco-front")
    public String getEcoFront() {
        return "eco-front"; // имя шаблона eco-front.html
    }
}
