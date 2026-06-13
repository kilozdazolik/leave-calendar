"use strict";

async function apiFetch(path, options = {}) {
    const res = await fetch(path, {
        headers: { 'Content-Type': 'application/json' },
        ...options
    });

    if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.error || `Request failed: ${res.status}`);
    }

    return res.status === 204 ? null : res.json();
}

const api = {
    getMembers:   () => apiFetch('/api/members'),
    getLeaves:    (params = {}) => apiFetch('/api/leaves?' + new URLSearchParams(params)),
    createLeave:  (data) => apiFetch('/api/leaves', { method: 'POST', body: JSON.stringify(data) }),
    updateStatus: (id, status) => apiFetch(`/api/leaves/${id}/status`, { method: 'PATCH', body: JSON.stringify({ status }) }),
    deleteLeave:  (id) => apiFetch(`/api/leaves/${id}`, { method: 'DELETE' }),
    getOnCall:    () => apiFetch('/api/oncall?weeks=8')
};