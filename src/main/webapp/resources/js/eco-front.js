const API_BASE = '/api/events';

let currentYear;
let currentMonth; // 0-11

// ================== API ==================

async function fetchEvents() {
    const response = await fetch(API_BASE);
    if (!response.ok) {
        throw new Error('Не удалось загрузить события');
    }
    return response.json(); // EventDto: { events, upcomingEventsQuantity, finishedEventsQuantity }
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
        throw new Error('Не удалось создать событие');
    }
}

async function deleteEvent(id) {
    const response = await fetch(`${API_BASE}/${id}`, {
        method: 'DELETE'
    });

    if (!response.ok) {
        throw new Error('Не удалось удалить событие');
    }
}

// ================== КАЛЕНДАРЬ ==================

const monthNames = [
    'Январь', 'Февраль', 'Март', 'Апрель',
    'Май', 'Июнь', 'Июль', 'Август',
    'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'
];

// Парсим дату события из строки startTime (формат типа "2025-11-30T10:00")
function getEventDate(event) {
    if (!event.startTime) {
        return null;
    }

    // Берём только часть YYYY-MM-DD
    const datePart = event.startTime.substring(0, 10);
    const date = new Date(datePart + 'T00:00:00');

    if (isNaN(date.getTime())) {
        return null;
    }

    return date;
}

// Группируем события по дате "YYYY-MM-DD"
function groupEventsByDay(events) {
    const map = {};

    events.forEach(event => {
        const date = getEventDate(event);
        if (!date) {
            return;
        }

        const year = date.getFullYear();
        const month = date.getMonth();

        // фильтруем по текущему месяцу
        if (year !== currentYear || month !== currentMonth) {
            return;
        }

        const key = date.toISOString().substring(0, 10); // "YYYY-MM-DD"

        if (!map[key]) {
            map[key] = [];
        }
        map[key].push(event);
    });

    return map;
}

// Рисуем календарь
function renderCalendar(events) {
    const monthYearLabel = document.getElementById('calendar-month-year');
    const calendarDaysContainer = document.getElementById('calendar-days');

    monthYearLabel.textContent = `${monthNames[currentMonth]} ${currentYear}`;

    // Очищаем
    calendarDaysContainer.innerHTML = '';

    const firstDayOfMonth = new Date(currentYear, currentMonth, 1);
    const lastDayOfMonth = new Date(currentYear, currentMonth + 1, 0);
    const daysInMonth = lastDayOfMonth.getDate();

    // В JS неделя с воскресенья (0), нам нужна с понедельника
    let startWeekDay = firstDayOfMonth.getDay(); // 0 (Вс) - 6 (Сб)
    if (startWeekDay === 0) {
        startWeekDay = 7;
    }

    const eventsByDay = groupEventsByDay(events);

    // Пустые ячейки до первого дня месяца (если месяц не начинается с понедельника)
    for (let i = 1; i < startWeekDay; i++) {
        const emptyCell = document.createElement('div');
        emptyCell.classList.add('calendar-day', 'calendar-day--empty');
        calendarDaysContainer.appendChild(emptyCell);
    }

    // Ячейки дней месяца
    for (let day = 1; day <= daysInMonth; day++) {
        const cell = document.createElement('div');
        cell.classList.add('calendar-day');

        const date = new Date(currentYear, currentMonth, day);
        const dateKey = date.toISOString().substring(0, 10); // "YYYY-MM-DD"

        const dayNumber = document.createElement('div');
        dayNumber.classList.add('calendar-day-number');
        dayNumber.textContent = day;
        cell.appendChild(dayNumber);

        // События этого дня
        const dayEvents = eventsByDay[dateKey] || [];
        const eventsContainer = document.createElement('div');
        eventsContainer.classList.add('calendar-events');

        dayEvents.forEach(event => {
            const eventEl = document.createElement('div');
            eventEl.classList.add('calendar-event');

            const timeText = (event.startTime && event.startTime.length >= 16)
                ? event.startTime.substring(11, 16) // "HH:MM"
                : '';

            const header = document.createElement('div');
            header.classList.add('calendar-event-header');
            header.textContent = timeText ? `${timeText} · ${event.summary}` : event.summary;

            const deleteBtn = document.createElement('button');
            deleteBtn.textContent = '×';
            deleteBtn.classList.add('calendar-event-delete');
            deleteBtn.addEventListener('click', async (e) => {
                e.stopPropagation();
                if (confirm('Удалить это событие?')) {
                    try {
                        await deleteEvent(event.id);
                        await loadAndRender();
                    } catch (err) {
                        alert(err.message);
                    }
                }
            });

            const headerWrapper = document.createElement('div');
            headerWrapper.classList.add('calendar-event-header-row');
            headerWrapper.appendChild(header);
            headerWrapper.appendChild(deleteBtn);

            eventEl.appendChild(headerWrapper);

            eventsContainer.appendChild(eventEl);
        });

        cell.appendChild(eventsContainer);
        calendarDaysContainer.appendChild(cell);
    }
}

// ================== СТАТИСТИКА + ФОРМА ==================

function renderStats(dto) {
    const upcomingSpan = document.getElementById('upcoming-count');
    const finishedSpan = document.getElementById('finished-count');

    upcomingSpan.textContent = dto.upcomingEventsQuantity;
    finishedSpan.textContent = dto.finishedEventsQuantity;
}

async function loadAndRender() {
    try {
        const dto = await fetchEvents();
        renderStats(dto);
        renderCalendar(dto.events);
    } catch (e) {
        alert(e.message);
    }
}

function setupForm() {
    const form = document.getElementById('add-event-form');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formData = new FormData(form);
        const eventData = {
            summary: formData.get('summary'),
            description: formData.get('description'),
            startTime: formData.get('startTime') || null,
            endTime: formData.get('endTime') || null,
            status: formData.get('status')
        };

        try {
            await createEvent(eventData);
            form.reset();
            await loadAndRender();
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
