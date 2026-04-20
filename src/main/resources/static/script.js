document.addEventListener('DOMContentLoaded', () => {
    fetchEvents();
    // Poll for live ticket updates every 5 seconds
    setInterval(fetchEvents, 5000);

    const modal = document.getElementById('booking-modal');
    const closeModalBtn = document.getElementById('close-modal');
    const bookingForm = document.getElementById('booking-form');

    closeModalBtn.addEventListener('click', () => {
        modal.classList.remove('active');
    });

    // Close modal if clicking outside
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.classList.remove('active');
        }
    });

    bookingForm.addEventListener('submit', handleBookingSubmit);
});

async function fetchEvents() {
    try {
        const response = await fetch('/api/events');
        if (!response.ok) throw new Error('Failed to load events');
        
        const events = await response.json();
        renderEvents(events);
    } catch (error) {
        console.error(error);
        if (document.getElementById('events-grid').children.length === 1) {
            document.getElementById('events-grid').innerHTML = '<div class="loading-state">Failed to load events. Make sure server is running.</div>';
        }
    }
}

function renderEvents(events) {
    const grid = document.getElementById('events-grid');
    grid.innerHTML = ''; // clear current

    events.forEach(event => {
        const dateObj = new Date(event.date);
        const formattedDate = dateObj.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
        
        const card = document.createElement('div');
        card.className = 'event-card';

        const isSoldOut = event.availableSeats <= 0;
        const lowStock = event.availableSeats > 0 && event.availableSeats < 50;
        
        const priceStr = event.price === 0 ? 'Free' : `$${event.price.toFixed(2)}`;

        card.innerHTML = `
            <div>
                <p class="event-date">${formattedDate}</p>
                <h3 class="event-title">${event.name}</h3>
                <p class="event-desc">${event.description}</p>
            </div>
            <div class="event-footer">
                <div class="price">${priceStr}</div>
                <div class="availability">
                    <span class="seats-count ${lowStock ? 'low' : ''}">${event.availableSeats}</span>
                    <span class="seats-label">seats left</span>
                </div>
            </div>
            <button class="book-btn" onclick="openModal(${event.id}, '${event.name}', '${priceStr}')" ${isSoldOut ? 'disabled' : ''}>
                ${isSoldOut ? 'Sold Out' : 'Grab Ticket'}
            </button>
        `;
        grid.appendChild(card);
    });
}

function openModal(id, name, price) {
    document.getElementById('modal-event-id').value = id;
    document.getElementById('modal-event-title').textContent = name;
    document.getElementById('modal-event-price').textContent = price;
    
    document.getElementById('email').value = '';
    document.getElementById('quantity').value = '1';
    
    document.getElementById('booking-modal').classList.add('active');
}

async function handleBookingSubmit(e) {
    e.preventDefault();
    
    const eventId = document.getElementById('modal-event-id').value;
    const email = document.getElementById('email').value;
    const quantity = document.getElementById('quantity').value;
    
    const btn = document.getElementById('submit-btn');
    const span = btn.querySelector('span');
    const loader = btn.querySelector('.loader');

    // UI Loading state
    btn.disabled = true;
    span.style.display = 'none';
    loader.style.display = 'block';

    try {
        const req = await fetch(`/api/events/${eventId}/reserve`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, quantity: parseInt(quantity) })
        });
        
        const data = await req.json();

        if (req.ok) {
            showToast('Ticket reserved successfully!', 'success');
            document.getElementById('booking-modal').classList.remove('active');
            fetchEvents(); // immediately refresh seats
        } else {
            showToast(data.message || 'Failed to book tickets.', 'error');
        }
    } catch (error) {
        showToast('Network error trying to book ticket.', 'error');
    } finally {
        // Reset UI
        btn.disabled = false;
        span.style.display = 'block';
        loader.style.display = 'none';
    }
}

function showToast(message, type) {
    const container = document.getElementById('notification-container');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;
    
    container.appendChild(toast);
    
    setTimeout(() => {
        toast.style.animation = 'fadeOut 0.3s forwards';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}
