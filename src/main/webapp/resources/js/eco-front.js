const API_BASE = '/api/events';

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

function renderEvents(dto) {
    const tbody = document.getElementById('events-table-body');
    const upcomingSpan = document.getElementById('upcoming-count');
    const finishedSpan = document.getElementById('finished-count');

    tbody.innerHTML = '';

    upcomingSpan.textContent = dto.upcomingEventsQuantity;
    finishedSpan.textContent = dto.finishedEventsQuantity;

    dto.events.forEach(event => {
        const tr = document.createElement('tr');

        const summaryTd = document.createElement('td');
        summaryTd.textContent = event.summary;

        const descTd = document.createElement('td');
        descTd.textContent = event.description;

        const startTd = document.createElement('td');
        startTd.textContent = event.startTime || 'Весь день';

        const endTd = document.createElement('td');
        endTd.textContent = event.endTime || 'Весь день';

        const actionsTd = document.createElement('td');
        const deleteBtn = document.createElement('button');
        deleteBtn.textContent = 'Удалить';
        deleteBtn.classList.add('admin-btn');
        deleteBtn.addEventListener('click', async () => {
            if (confirm('Удалить это событие?')) {
                try {
                    await deleteEvent(event.id);
                    await loadAndRender(); // перезагружаем список
                } catch (e) {
                    alert(e.message);
                }
            }
        });

        actionsTd.appendChild(deleteBtn);

        tr.appendChild(summaryTd);
        tr.appendChild(descTd);
        tr.appendChild(startTd);
        tr.appendChild(endTd);
        tr.appendChild(actionsTd);

        tbody.appendChild(tr);
    });
}

async function loadAndRender() {
    try {
        const dto = await fetchEvents();
        renderEvents(dto);
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

document.addEventListener('DOMContentLoaded', () => {
    setupForm();
    loadAndRender();
});
