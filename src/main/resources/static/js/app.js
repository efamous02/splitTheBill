const $ = id => document.getElementById(id);
const money = n => "$" + Number(n).toFixed(2);   // change "$" for your currency
let rid = null, receipt = { lineItems: [] };
 
async function api(method, path, body) {
  const res = await fetch(`/api/receipts/${rid}${path}`, {
    method,
    headers: body ? { "Content-Type": "application/json" } : {},
    body: body ? JSON.stringify(body) : undefined
  });
  if (!res.ok && !(method === "GET" && res.status === 404)) {
    const detail = (await res.text()).slice(0, 200);
    throw new Error(`${method} ${path || "/"} failed (${res.status}) ${detail}`);
  }
  return res;
}
 
async function load() {
  $("msg").textContent = "";
  try {
    const res = await api("GET", "");
    receipt = res.status === 404 ? { id: rid, lineItems: [] } : await res.json();
  } catch (e) { $("msg").textContent = e.message; return; }
  render();
}
 
async function act(fn) {
  try { await fn(); await load(); }
  catch (e) { $("msg").textContent = e.message; }
}
 
function render() {
  $("body").hidden = false; $("list").hidden = true;
  const items = receipt.lineItems || [];
  const names = [...new Set(items.flatMap(people))];
  $("people").innerHTML = names.map(n => `<option value="${esc(n)}">`).join("");
 
  $("items").innerHTML = items.length ? items.map(i => `
    <div class="item" data-id="${i.id}">
      <div class="row">
        <span class="name">${esc(i.name)}</span>
        <span class="qty num">
          <button class="icon" data-a="dec" aria-label="Decrease quantity" ${i.quantity <= 1 ? "disabled" : ""}>−</button>
          ${i.quantity}
          <button class="icon" data-a="inc" aria-label="Increase quantity">+</button>
        </span>
        <span class="price num">${money(i.quantity * i.unitPrice)}</span>
        <button class="icon" data-a="del" aria-label="Delete ${esc(i.name)}">✕</button>
      </div>
      <div class="assign">
        ${people(i).length
          ? people(i).map(p => `<span class="chip">${esc(p)}<button class="icon" data-a="unassign" data-person="${esc(p)}" aria-label="Remove ${esc(p)}">✕</button></span>`).join("")
          : `<span class="muted">Unassigned</span>`}
        <input list="people" placeholder="Assign to…" aria-label="Person to assign">
        <button class="ghost" data-a="assign">Assign</button>
      </div>
    </div>`).join("")
    : `<div class="empty">Receipt #${rid} is empty.<br>Add its first item above and the receipt is saved automatically.</div>`;
 
  const total = items.reduce((s, i) => s + i.quantity * i.unitPrice, 0);
  const by = {};
  items.forEach(i => {
    const ppl = people(i), cost = i.quantity * i.unitPrice;
    (ppl.length ? ppl : ["Unassigned"]).forEach(k => { by[k] = (by[k] || 0) + cost / Math.max(ppl.length, 1); });
  });
  $("totals").innerHTML = items.length
    ? Object.entries(by).map(([k, v]) => `<div class="muted"><span>${esc(k)}</span><span class="num">${money(v)}</span></div>`).join("")
      + `<div class="grand"><span>Total</span><span class="num">${money(total)}</span></div>` : "";
}
 
const people = i => Array.isArray(i.personAssignedTo) ? i.personAssignedTo.filter(Boolean) : (i.personAssignedTo ? [i.personAssignedTo] : []);
const esc = s => String(s).replace(/[&<>"']/g, c => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
 
function open(id) { rid = String(id); location.hash = rid; $("list").hidden = true; load(); }
 
async function showList() {
  $("msg").textContent = "";
  $("body").hidden = true;
  rid = null; location.hash = "list";
  let all;
  try {
    const res = await fetch("/api/receipts");
    if (!res.ok) throw new Error(`GET /api/receipts failed (${res.status})`);
    all = await res.json();
  } catch (e) { $("msg").textContent = e.message; return; }
  $("list").hidden = false;
  $("list").innerHTML = `<h2 class="list-title">Existing receipts</h2>` + (all.length ? all.map(r => {
    const items = r.lineItems || [];
    const total = items.reduce((s, i) => s + i.quantity * i.unitPrice, 0);
    return `<button type="button" class="rcpt" data-id="${esc(r.id)}">
      <span class="id">Receipt #${esc(r.id)}</span>
      <span class="meta">${items.length} item${items.length === 1 ? "" : "s"}</span>
      <span class="amt num">${money(total)}</span></button>`;
  }).join("") : `<div class="empty">No receipts yet.<br>Choose New receipt to start one.</div>`);
}
 
$("listBtn").addEventListener("click", showList);
$("list").addEventListener("click", e => {
  const b = e.target.closest("button[data-id]");
  if (b) open(b.dataset.id);
});
 
 
// New receipt: use the first number that has no items yet
$("newBtn").addEventListener("click", async () => {
  $("msg").textContent = "";
  for (let n = 1; n <= 500; n++) {
    let free = true;
    try {
      const r = await fetch(`/api/receipts/${n}`);
      if (r.ok) free = !(((await r.json()).lineItems || []).length);
    } catch (e) { $("msg").textContent = "Could not reach the server."; return; }
    if (free) { open(n); return; }
  }
  $("msg").textContent = "Couldn't find an unused receipt number.";
});
 
$("addForm").addEventListener("submit", e => {
  e.preventDefault();
  act(async () => {
    const nextId = Math.max(0, ...receipt.lineItems.map(i => i.id)) + 1;
    await api("POST", "/items", {
      id: nextId,
      name: $("iname").value.trim(),
      quantity: Number($("iqty").value),
      unitPrice: Number($("iprice").value),
      personAssignedTo: []
    });
    $("addForm").reset(); $("iqty").value = 1; $("iname").focus();
  });
});
 
$("items").addEventListener("click", e => {
  const btn = e.target.closest("button[data-a]");
  if (!btn) return;
  const el = btn.closest(".item"), id = el.dataset.id;
  const item = receipt.lineItems.find(i => String(i.id) === id);
  const a = btn.dataset.a;
  if (a === "del") act(() => api("DELETE", `/items/${id}`));
  if (a === "inc") act(() => api("PATCH", `/items/${id}`, { quantity: item.quantity + 1 }));
  if (a === "dec") act(() => api("PATCH", `/items/${id}`, { quantity: item.quantity - 1 }));
  if (a === "unassign") {
    const rest = people(item).filter(p => p !== btn.dataset.person);
    act(() => rest.length ? api("PUT", `/items/${id}/assignment`, { personAssignedTo: rest })
                          : api("DELETE", `/items/${id}/assignment`));
  }
  if (a === "assign") assign(el, id);
});
 
$("items").addEventListener("keydown", e => {
  if (e.key === "Enter" && e.target.matches(".assign input")) {
    const el = e.target.closest(".item"); assign(el, el.dataset.id);
  }
});
 
function assign(el, id) {
  const person = el.querySelector(".assign input").value.trim();
  if (!person) return;
  const current = people(receipt.lineItems.find(i => String(i.id) === id));
  if (current.includes(person)) { el.querySelector(".assign input").value = ""; return; }
  act(() => api("PUT", `/items/${id}/assignment`, { personAssignedTo: [...current, person] }));
}
 
if (location.hash === "#list") showList();
else if (location.hash.length > 1) { rid = location.hash.slice(1); load(); }
 