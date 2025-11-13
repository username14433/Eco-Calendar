package org.rockend.eco_calendar_web_application_demo.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.rockend.eco_calendar_web_application_demo.entity.Event;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class EventDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Event> getEvents() {
        Query query = entityManager.createQuery("SELECT e FROM Event e");
        return query.getResultList();
    }

    public void saveEvent(Event event) {
        entityManager.persist(event);
    }

    public void removeEvent(int id) {
        Query query = entityManager.createQuery("DELETE FROM Event e WHERE e.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }

    public Event findEventById(long id) {
        Event event = entityManager.find(Event.class, id);
        return event;
    }


}
