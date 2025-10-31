package org.rockend.eco_calendar_web_application_demo.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.rockend.eco_calendar_web_application_demo.entity.Event;
import org.rockend.eco_calendar_web_application_demo.entity.EventStatus;
import org.rockend.eco_calendar_web_application_demo.entity.dto.EventDto;
import org.rockend.eco_calendar_web_application_demo.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @PostMapping("/add")
    public String addEvent(
            @RequestParam String summary,
            @RequestParam String description,
            @RequestParam String startDateTime,
            @RequestParam String endDateTime,
            @CookieValue(value = "isAdmin", defaultValue = "none") String isAdminCookie
    ) {
        String formattedStartDateTime = eventService.formatDateTime(startDateTime);
        String formattedEndDateTime = eventService.formatDateTime(endDateTime);
        eventService.addEvent(summary, description, formattedStartDateTime, formattedEndDateTime, EventStatus.AWAITING);

        if (isAdminCookie.equals("true")) {
            return "redirect:/admin-panel";
        }

        return "redirect:/calendar";
    }

    @GetMapping("/login-to-admin")
    public String loginToAdminPanel(@CookieValue(value = "isAdmin", defaultValue = "none")  String isAdminCookie) {
        if (isAdminCookie.equals("true")) {
            return "redirect:/admin-panel";
        }
        return "login-to-admin";
    }


    //TODO: Сделать так, чтобы если пользователь вошёл в аккаунт админа, ему была доступна функция удаления событий
    //TODO: Сделать так, чтобы пользователь мог ввести свой логин (почту) и ему приходили оповещения о ближайших событиях



    @PostMapping("/admin-panel")
    public String adminPanel(@RequestParam(name = "loginId") String adminId,
                             RedirectAttributes redirectAttributes,
                             HttpServletResponse servletResponse) {
        if (!adminId.equals("admin")) {
            redirectAttributes.addFlashAttribute("error", "Администратор с таким идентификатором не найден!");
            return "redirect:/login-to-admin";
        }
        Cookie isAdminCookie = new Cookie("isAdmin", "true");
        isAdminCookie.setPath("/");
        isAdminCookie.setMaxAge(3600);
        servletResponse.addCookie(isAdminCookie);
        return "admin-panel";
    }

    @GetMapping("admin-panel")
    public String getAdminPanel() {
        return "admin-panel";
    }

    @PostMapping("/delete-event")
    public String deleteEvent(@RequestParam("id") long eventId) {
        eventService.deleteEvent(eventId);

        return "redirect:/admin-panel";
    }
}
