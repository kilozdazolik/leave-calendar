"use strict";

const state = {
    members: [],
    leaveRequests: [],
    onCallSchedule: []
};

let currentNavDate = new Date();

// --- DOM RENDER FUNCTIONS ---

function renderLeaveRequests() {
    const container = document.getElementById('leave-requests-container');

    if (state.leaveRequests.length === 0) {
        container.innerHTML = `<div class="text-center text-muted py-5 bg-white rounded shadow-sm border">No leave requests found.</div>`;
        return;
    }

    const rows = state.leaveRequests.map(r => `
        <tr>
            <td class="align-middle">${r.teamMember.name}</td>
            <td class="align-middle">${r.startDate}</td>
            <td class="align-middle">${r.endDate}</td>
            <td class="align-middle">${r.reason}</td>
            <td class="align-middle">
                <span class="badge bg-${r.status === 'APPROVED' ? 'success' : r.status === 'REJECTED' ? 'danger' : 'secondary'}">${r.status}</span>
            </td>
            <td class="d-flex gap-1">
                ${r.status !== 'APPROVED' ? `<button class="btn btn-sm btn-success" onclick="updateStatus(${r.id}, 'APPROVED')">Approve</button>` : ''}
                ${r.status !== 'REJECTED' ? `<button class="btn btn-sm btn-danger" onclick="updateStatus(${r.id}, 'REJECTED')">Reject</button>` : ''}
                <button class="btn btn-sm btn-outline-secondary" onclick="deleteLeave(${r.id})">Delete</button>
            </td>
        </tr>
    `).join('');

    container.innerHTML = `
        <table class="table table-hover bg-white shadow-sm rounded border">
            <thead class="table-light">
                <tr><th>Member</th><th>Start</th><th>End</th><th>Reason</th><th>Status</th><th>Actions</th></tr>
            </thead>
            <tbody>${rows}</tbody>
        </table>`;
}

function renderOnCall() {
    const container = document.getElementById('on-call-container');
    const rows = state.onCallSchedule.map(week => `
        <tr class="${week.hasConflict ? 'table-warning' : ''}">
            <td class="align-middle">${week.weekStart}</td>
            <td class="align-middle">${week.weekEnd}</td>
            <td class="align-middle">${week.teamMember.name}</td>
            <td class="align-middle">
                ${week.hasConflict
        ? '<span class="badge bg-warning text-dark">⚠ On Leave</span>'
        : '<span class="badge bg-success">Available</span>'}
            </td>
        </tr>
    `).join('');

    container.innerHTML = `
        <table class="table table-hover bg-white shadow-sm rounded border">
            <thead class="table-light">
                <tr><th>Week Start</th><th>Week End</th><th>On-Call</th><th>Status</th></tr>
            </thead>
            <tbody>${rows}</tbody>
        </table>`;
}

function renderCalendar() {
    const container = document.getElementById('calendar-days');
    const label = document.getElementById('calendar-month-label');

    const year = currentNavDate.getFullYear();
    const month = currentNavDate.getMonth();

    label.textContent = currentNavDate.toLocaleString('default', { month: 'long', year: 'numeric' });

    const firstDay = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const startPadding = firstDay === 0 ? 6 : firstDay - 1;

    let html = '';

    for (let i = 0; i < startPadding; i++) {
        html += `<div class="calendar-day-cell bg-light"></div>`;
    }

    for (let day = 1; day <= daysInMonth; day++) {
        const currentDateString = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;

        const leavesToday = state.leaveRequests.filter(req =>
            currentDateString >= req.startDate && currentDateString <= req.endDate
        );

        const leaveBadges = leavesToday.map(req => {
            const color = req.status === 'APPROVED' ? 'bg-success' : req.status === 'REJECTED' ? 'bg-danger' : 'bg-secondary';
            return `<div class="badge ${color} d-block mb-1 text-truncate" title="${req.reason}">${req.teamMember.name}</div>`;
        }).join('');

        const onCallThisWeek = state.onCallSchedule.find(week =>
            currentDateString >= week.weekStart && currentDateString <= week.weekEnd
        );

        let onCallBadge = '';
        if (onCallThisWeek) {
            const conflictClass = onCallThisWeek.hasConflict ? 'border-danger text-danger bg-white' : 'border-dark text-dark bg-light';

            onCallBadge = `
                <div class="badge border ${conflictClass} d-block mb-1 text-truncate" title="On Call">
                    <i class="bi bi-telephone-fill me-1"></i>${onCallThisWeek.teamMember.name}
                </div>
            `;
        }
        html += `
            <div class="calendar-day-cell">
                <div class="fw-bold text-end mb-1 text-secondary">${day}</div>
                ${onCallBadge}
                ${leaveBadges}
            </div>
        `;
    }

    container.innerHTML = html;
}

function renderMemberDropdowns() {
    const options = state.members.map(m => `<option value="${m.id}">${m.name}</option>`).join('');
    document.getElementById('member-select').innerHTML = options;
    document.getElementById('filter-member').innerHTML = '<option value="">All Members</option>' + options;
}

// --- ACTIONS & EVENT LISTENERS ---

async function loadLeaves(params = {}) {
    state.leaveRequests = await api.getLeaves(params);
    renderLeaveRequests();
    renderCalendar();
}

async function loadOnCall() {
    state.onCallSchedule = await api.getOnCall();
    renderOnCall();
}

async function updateStatus(id, status) {
    try {
        await api.updateStatus(id, status);
        await loadLeaves();
        await loadOnCall();
    } catch (err) { alert(err.message); }
}

async function deleteLeave(id) {
    if (!confirm('Delete this leave request?')) return;
    try {
        await api.deleteLeave(id);
        await loadLeaves();
        await loadOnCall();
    } catch (err) { alert(err.message); }
}

async function applyFilters() {
    const memberId = document.getElementById('filter-member').value;
    const status = document.getElementById('filter-status').value;
    const params = {};
    if (memberId) params.memberId = memberId;
    if (status) params.status = status;
    await loadLeaves(params);
}

async function clearFilters() {
    document.getElementById('filter-member').value = '';
    document.getElementById('filter-status').value = '';
    await loadLeaves();
}

function changeMonth(offset) {
    currentNavDate.setMonth(currentNavDate.getMonth() + offset);
    renderCalendar();
}

// Form Submission
document.getElementById('leave-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const errorEl = document.getElementById('form-error');
    errorEl.classList.add('d-none');

    const startDate = document.getElementById('start-date').value;
    const endDate = document.getElementById('end-date').value;

    if (endDate < startDate) {
        errorEl.textContent = 'End date must be on or after start date.';
        errorEl.classList.remove('d-none');
        return;
    }

    try {
        await api.createLeave({
            teamMemberId: parseInt(document.getElementById('member-select').value),
            startDate,
            endDate,
            reason: document.getElementById('reason').value.trim()
        });

        e.target.reset();
        await loadLeaves();
        await loadOnCall();
        document.querySelector('[data-tab="leave-requests"]').click();
    } catch (err) {
        errorEl.textContent = err.message;
        errorEl.classList.remove('d-none');
    }
});

// Tab Switching Logic
document.querySelectorAll('.nav-link').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.tab-content').forEach(s => s.classList.add('d-none'));
        document.querySelectorAll('.nav-link').forEach(b => b.classList.remove('active'));

        document.getElementById(btn.dataset.tab).classList.remove('d-none');
        btn.classList.add('active');
    });
});

// --- INITIALIZE ---
async function init() {
    try {
        const [members, leaves, onCall] = await Promise.all([
            api.getMembers(),
            api.getLeaves(),
            api.getOnCall()
        ]);

        state.members = members;
        state.leaveRequests = leaves;
        state.onCallSchedule = onCall;

        renderMemberDropdowns();
        renderLeaveRequests();
        renderOnCall();
        renderCalendar();
    } catch (err) {
        console.error('Failed to initialize app:', err);
    }
}

document.addEventListener('DOMContentLoaded', init);