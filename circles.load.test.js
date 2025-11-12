import http from "k6/http";
import { check, sleep } from "k6";

// --------- Config ---------
const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const CIRCLES = parseInt(__ENV.CIRCLES || "10", 10);
const MEMBERS_PER_CIRCLE = parseInt(__ENV.MEMBERS_PER_CIRCLE || "5", 10);
const HEADERS = { "Content-Type": "application/json" };

// --------- k6 Options ---------
export const options = {
    setupTimeout: "3m",
    scenarios: {
        ramp: {
            executor: "ramping-vus",
            startVUs: 0,
            stages: [
                { duration: "20s", target: 10 },
                { duration: "1m", target: 10 },
                { duration: "20s", target: 0 },
            ],
            gracefulRampDown: "10s",
        },
    },
    thresholds: {
        http_req_failed: ["rate<0.01"],
        http_req_duration: ["p(95)<500"],
    },
};

// --------- Helpers ---------
const circleTypes = ["FAMILY", "FRIENDS", "WORK", "HOBBY", "COMMUNITY", "OTHER"];
const privacyTypes = ["PUBLIC", "PRIVATE", "INVITE_ONLY"];
const roles = ["ADMIN", "MEMBER", "VIEWER"];

function randOf(arr) { return arr[Math.floor(Math.random() * arr.length)]; }
function randName(prefix = "Circle") { return `${prefix}-${Math.random().toString(36).slice(2, 8)}`; }
function uuidv4() { // simple non-crypto uuid
    return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, c => {
        const r = (Math.random() * 16) | 0, v = c === "x" ? r : (r & 0x3) | 0x8;
        return v.toString(16);
    });
}
function randUser() {
    return {
        userId: uuidv4(),
        userName: `user_${Math.random().toString(36).slice(2, 10)}`,
        userAvatar: `https://picsum.photos/seed/${Math.random().toString(36).slice(2, 8)}/100/100`,
        nickname: `nick_${Math.random().toString(36).slice(2, 6)}`
    };
}

// --------- setup(): seed data ---------
export function setup() {
    const created = [];

    for (let i = 0; i < CIRCLES; i++) {
        const body = {
            name: randName("Circle"),
            description: "Load test circle",
            circleType: randOf(circleTypes),
            privacy: randOf(privacyTypes),
            avatarUrl: "",
            settings: { color: "blue" }
        };

        const res = http.post(`${BASE_URL}/api/circles`, JSON.stringify(body), { headers: HEADERS });
        check(res, {
            "createCircle 200": (r) => r.status === 200,
            "createCircle has id": (r) => !!(r.json()?.id),
        });

        const circle = res.json();
        const circleId = circle?.id;
        const inviteCode = circle?.inviteCode;

        // Add members (first one ADMIN)
        const admin = randUser();
        const adminReq = { ...admin, role: "ADMIN" };
        const addAdmin = http.post(`${BASE_URL}/api/circles/${circleId}/members`, JSON.stringify(adminReq), { headers: HEADERS });
        check(addAdmin, { "add ADMIN 200": (r) => r.status === 200 });

        const members = [admin];
        for (let m = 0; m < MEMBERS_PER_CIRCLE - 1; m++) {
            const u = randUser();
            const addReq = { ...u, role: randOf(["MEMBER", "VIEWER"]) };
            const addRes = http.post(`${BASE_URL}/api/circles/${circleId}/members`, JSON.stringify(addReq), { headers: HEADERS });
            check(addRes, { "add member 200": (r) => r.status === 200 });
            members.push(u);
        }

        created.push({ circleId, inviteCode, members });
    }

    return { created, baseUrl: BASE_URL };
}

// --------- default(): per-iteration user flow ---------
export default function (data) {
    const BASE = data?.baseUrl || BASE_URL;
    const bag = data?.created || [];

    if (!bag.length) {
        const res = http.get(`${BASE}/api/circles`);
        check(res, { "list circles 200": (r) => r.status === 200 });
        sleep(1);
        return;
    }

    const item = randOf(bag);
    const circleId = item.circleId;

    // 1) Get details
    const getRes = http.get(`${BASE}/api/circles/${circleId}`);
    check(getRes, {
        "get circle 200": (r) => r.status === 200,
        "get has name": (r) => !!r.json()?.name,
    });

    // 2) Search with query params
    const searchRes = http.get(`${BASE}/api/circles?name=${encodeURIComponent("Circle")}&privacy=PUBLIC`);
    check(searchRes, { "search 200": (r) => r.status === 200 });

    // 3) PATCH part of circle
    const patchReq = { description: "patched by k6", privacy: randOf(privacyTypes) };
    const patchRes = http.patch(`${BASE}/api/circles/${circleId}`, JSON.stringify(patchReq), { headers: HEADERS });
    check(patchRes, { "patch 200": (r) => r.status === 200 });

    // 4) GET members
    const membersRes = http.get(`${BASE}/api/circles/${circleId}/members`);
    check(membersRes, { "members 200": (r) => r.status === 200 });

    // 5) Add a member (sometimes)
    if (Math.random() < 0.3) {
        const u = randUser();
        const addReq = { ...u, role: randOf(roles) };
        const addRes = http.post(`${BASE}/api/circles/${circleId}/members`, JSON.stringify(addReq), { headers: HEADERS });
        check(addRes, { "add member (loop) 200/409": (r) => r.status === 200 || r.status === 409 });
    }

    // 6) Update an existing member (if we have one)
    if (item.members?.length) {
        const target = randOf(item.members);
        const updReq = { nickname: `upd_${Math.random().toString(36).slice(2, 6)}` };
        const updRes = http.patch(`${BASE}/api/circles/${circleId}/members/${target.userId}`, JSON.stringify(updReq), { headers: HEADERS });
        check(updRes, { "update member 200/404": (r) => r.status === 200 || r.status === 404 });
    }

    // 7) Get stats
    const statsRes = http.get(`${BASE}/api/circles/${circleId}/stats`);
    check(statsRes, { "stats 200": (r) => r.status === 200 });

    // 8) Join by invite code (sometimes)
    if (item.inviteCode && Math.random() < 0.2) {
        const joinBody = { ...randUser() };
        const joinRes = http.post(`${BASE}/api/circles/join/${encodeURIComponent(item.inviteCode)}`, JSON.stringify(joinBody), { headers: HEADERS });
        check(joinRes, { "join by code 200/409": (r) => r.status === 200 || r.status === 409 });
    }

    sleep(0.5);
}

// --------- teardown(): optional cleanup ---------
// export function teardown(data) {
//   const BASE = data?.baseUrl || BASE_URL;
//   for (const item of data?.created || []) {
//     const del = http.del(`${BASE}/api/circles/${item.circleId}`);
//     check(del, { "delete circle 200/204": (r) => r.status === 200 || r.status === 204 });
//   }
// }
