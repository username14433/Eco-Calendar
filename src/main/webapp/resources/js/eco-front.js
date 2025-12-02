const API_BASE = '/api/events';

let currentYear;
let currentMonth;

const monthNames = [
    'Январь', 'Февраль', 'Март', 'Апрель',
    'Май', 'Июнь', 'Июль', 'Август',
    'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'
];


async function fetchEvents() {
    const response = await fetch(API_BASE);
    if (!response.ok) {
        throw new Error('Не удалось загрузить события');
    }
    return response.json();
}

async function createEvent(eventData) {
    const response = await fetch(API_BASE, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(eventData)
    });

    if (!response.ok) {
        const text = await response.text();
        throw new Error('Не удалось создать событие: ' + text);
    }
}

async function deleteEvent(id) {
    const response = await fetch(`${API_BASE}/${id}`, {
        method: 'DELETE'
    });

    if (!response.ok) {
        const text = await response.text();
        throw new Error('Не удалось удалить событие: ' + text);
    }
}


function parseDateOnly(dateTimeStr) {
    if (!dateTimeStr) return null;
    const datePart = dateTimeStr.substring(0, 10);
    const date = new Date(datePart + 'T00:00:00');
    return isNaN(date.getTime()) ? null : date;
}

function formatTime(dateTimeStr) {
    if (!dateTimeStr || dateTimeStr.length < 16) return '';
    return dateTimeStr.substring(11, 16); // "HH:MM"
}

function formatDateHuman(date) {
    const dd = String(date.getDate()).padStart(2, '0');
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const yyyy = date.getFullYear();
    return `${dd}.${mm}.${yyyy}`;
}

function groupEventsByDay(events) {
    const map = {};
    events.forEach(event => {
        const date = parseDateOnly(event.startTime);
        if (!date) return;
        const key = date.toISOString().substring(0, 10);
        if (!map[key]) {
            map[key] = [];
        }
        map[key].push(event);
    });
    return map;
}


function renderCalendar(events, onDayClick) {
    const monthYearLabel = document.getElementById('calendar-month-year');
    const calendarDaysContainer = document.getElementById('calendar-days');

    monthYearLabel.textContent = `${monthNames[currentMonth]} ${currentYear}`;
    calendarDaysContainer.innerHTML = '';

    const firstDayOfMonth = new Date(currentYear, currentMonth, 1);
    const lastDayOfMonth = new Date(currentYear, currentMonth + 1, 0);
    const daysInMonth = lastDayOfMonth.getDate();

    let startWeekDay = firstDayOfMonth.getDay();
    if (startWeekDay === 0) startWeekDay = 7;

    const eventsByDay = groupEventsByDay(events);

    const today = new Date();
    const todayY = today.getFullYear();
    const todayM = today.getMonth();
    const todayD = today.getDate();

    for (let i = 1; i < startWeekDay; i++) {
        const emptyCell = document.createElement('div');
        emptyCell.classList.add('calendar-day', 'calendar-day--empty');
        calendarDaysContainer.appendChild(emptyCell);
    }

    for (let day = 1; day <= daysInMonth; day++) {
        const date = new Date(currentYear, currentMonth, day);
        const dateKey = date.toISOString().substring(0, 10);

        const dayEventsAll = eventsByDay[dateKey] || [];
        const dayEvents = dayEventsAll.filter(e => {
            const d = parseDateOnly(e.startTime);
            return d && d.getMonth() === currentMonth && d.getFullYear() === currentYear;
        });

        const cell = document.createElement('div');
        cell.classList.add('calendar-day');

        if (currentYear === todayY && currentMonth === todayM && day === todayD) {
            cell.classList.add('calendar-day--today');
        }

        const upcomingEvents = dayEvents.filter(e => e.status === 'AWAITING');
        const finishedEvents = dayEvents.filter(e => e.status === 'FINISHED');

        if (upcomingEvents.length > 0) {
            cell.classList.add('calendar-day--upcoming');
        } else if (finishedEvents.length > 0) {
            cell.classList.add('calendar-day--finished');
        }

        const dayNumber = document.createElement('div');
        dayNumber.classList.add('calendar-day-number');
        dayNumber.textContent = day;
        cell.appendChild(dayNumber);

        const eventsContainer = document.createElement('div');
        eventsContainer.classList.add('calendar-events');

        const eventsToShow = dayEvents.slice(0, 3);

        eventsToShow.forEach(event => {
            const eventEl = document.createElement('div');
            eventEl.classList.add('calendar-event');

            if (event.status === 'FINISHED') {
                eventEl.classList.add('calendar-event--finished');
            } else {
                eventEl.classList.add('calendar-event--upcoming');
            }

            const timeText = formatTime(event.startTime);
            eventEl.textContent = timeText
                ? `${timeText} · ${event.summary}`
                : event.summary;

            eventsContainer.appendChild(eventEl);
        });

        if (dayEvents.length > 3) {
            const moreEl = document.createElement('div');
            moreEl.classList.add('calendar-event-more');
            moreEl.textContent = `+${dayEvents.length - 3} ещё`;
            eventsContainer.appendChild(moreEl);
        }

        cell.appendChild(eventsContainer);

        cell.addEventListener('click', () => {
            onDayClick(new Date(date), dayEvents);
        });

        calendarDaysContainer.appendChild(cell);
    }
}


function renderDayDetails(date, eventsForDay) {
    const titleEl = document.getElementById('day-details-title');
    const contentEl = document.getElementById('day-details-content');

    if (!date) {
        titleEl.textContent = 'Выберите день';
        contentEl.className = 'day-details-empty';
        contentEl.textContent = 'Пока ничего не выбрано';
        return;
    }

    titleEl.textContent = `События на ${formatDateHuman(date)}`;

    contentEl.innerHTML = '';
    contentEl.className = 'day-details-content';

    if (!eventsForDay || eventsForDay.length === 0) {
        contentEl.textContent = 'На этот день нет событий';
        return;
    }

    eventsForDay.forEach(event => {
        const item = document.createElement('div');
        item.classList.add('day-details-item');

        const header = document.createElement('div');
        header.classList.add('day-details-item-header');

        const title = document.createElement('div');
        title.classList.add('day-details-item-title');
        const timeFrom = formatTime(event.startTime);
        const timeTo = formatTime(event.endTime);
        const timeSpan = timeFrom && timeTo ? `${timeFrom}–${timeTo}` :
            timeFrom ? timeFrom : '';

        title.textContent = timeSpan ? `${timeSpan} · ${event.summary}` : event.summary;

        const status = document.createElement('div');
        status.classList.add('day-details-item-status');
        status.textContent = event.status === 'FINISHED' ? 'Завершено' : 'Предстоит';

        header.appendChild(title);
        header.appendChild(status);

        const desc = document.createElement('div');
        desc.classList.add('day-details-item-desc');
        desc.textContent = event.description;

        item.appendChild(header);
        item.appendChild(desc);

        if (window.IS_ADMIN) {
            const deleteBtn = document.createElement('button');
            deleteBtn.textContent = 'Удалить';
            deleteBtn.classList.add('admin-btn', 'day-details-delete-btn');
            deleteBtn.addEventListener('click', async () => {
                if (confirm('Удалить это событие?')) {
                    try {
                        await deleteEvent(event.id);
                        await loadAndRender(date);
                    } catch (e) {
                        alert(e.message);
                    }
                }
            });
            item.appendChild(deleteBtn);
        }

        contentEl.appendChild(item);
    });
}

function renderUpcomingList(events) {
    const listEl = document.getElementById('upcoming-list');
    listEl.innerHTML = '';

    const upcoming = events
        .filter(e => e.status === 'AWAITING' && e.startTime)
        .sort((a, b) => new Date(a.startTime) - new Date(b.startTime))
        .slice(0, 5);

    if (upcoming.length === 0) {
        const li = document.createElement('li');
        li.textContent = 'Нет ближайших событий';
        listEl.appendChild(li);
        return;
    }

    upcoming.forEach(event => {
        const li = document.createElement('li');
        const date = parseDateOnly(event.startTime);
        const dateText = date ? formatDateHuman(date) : '';
        const timeText = formatTime(event.startTime);
        li.textContent = `${dateText} ${timeText ? timeText + ' ' : ''}— ${event.summary}`;
        listEl.appendChild(li);
    });
}


function renderStats(dto) {
    document.getElementById('upcoming-count').textContent = dto.upcomingEventsQuantity;
    document.getElementById('finished-count').textContent = dto.finishedEventsQuantity;
}

async function loadAndRender(selectedDateForDetails = null) {
    try {
        const dto = await fetchEvents();
        renderStats(dto);

        renderCalendar(dto.events, (date, eventsForDay) => {
            renderDayDetails(date, eventsForDay);
        });

        renderUpcomingList(dto.events);

        if (selectedDateForDetails) {
            const keyDate = new Date(selectedDateForDetails.getTime());
            const eventsByDay = groupEventsByDay(dto.events);
            const key = keyDate.toISOString().substring(0, 10);
            const eventsForDay = eventsByDay[key] || [];
            renderDayDetails(keyDate, eventsForDay);
        } else {
            renderDayDetails(null, []);
        }
    } catch (e) {
        alert(e.message);
    }
}

function setupForm() {
    if (!window.IS_ADMIN) {
        return;
    }

    const form = document.getElementById('add-event-form');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formData = new FormData(form);
        const summary = formData.get('summary').trim();
        const description = formData.get('description').trim();
        const startTime = formData.get('startTime');
        const endTime = formData.get('endTime');
        const status = formData.get('status');

        if (!summary || !description) {
            alert('Заполните название и описание.');
            return;
        }

        if (!startTime || !endTime) {
            alert('Укажите время начала и конца.');
            return;
        }

        const startDate = new Date(startTime);
        const endDate = new Date(endTime);

        if (isNaN(startDate.getTime()) || isNaN(endDate.getTime())) {
            alert('Неверный формат даты/времени.');
            return;
        }

        if (endDate < startDate) {
            alert('Время окончания не может быть раньше начала.');
            return;
        }

        const eventData = { summary, description, startTime, endTime, status };

        try {
            await createEvent(eventData);
            form.reset();
            await loadAndRender(startDate);
        } catch (err) {
            alert(err.message);
        }
    });
}

function setupMonthNavigation() {
    const prevBtn = document.getElementById('prev-month');
    const nextBtn = document.getElementById('next-month');

    prevBtn.addEventListener('click', async () => {
        if (currentMonth === 0) {
            currentMonth = 11;
            currentYear--;
        } else {
            currentMonth--;
        }
        await loadAndRender();
    });

    nextBtn.addEventListener('click', async () => {
        if (currentMonth === 11) {
            currentMonth = 0;
            currentYear++;
        } else {
            currentMonth++;
        }
        await loadAndRender();
    });
}

document.addEventListener('DOMContentLoaded', () => {
    const now = new Date();
    currentYear = now.getFullYear();
    currentMonth = now.getMonth();

    setupForm();
    setupMonthNavigation();
    loadAndRender();
});
