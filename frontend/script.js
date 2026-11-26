const API = "http://localhost:8080/tarefa";
const PER = 8;
const $ = s => document.querySelector(s);
const S = {all: [], q: "", filter: "todas", sort: "venc", page: 1, editing: null, deleting: null, loading: true};
const MES = ["jan","fev","mar","abr","mai","jun","jul","ago","set","out","nov","dez"];
const IC = {
    check: '<path d="M5 12.5l4.5 4.5L19 7.5"/>',
    edit: '<path d="M4 20h4L19 9l-4-4L4 16v4z"/>',
    trash: '<path d="M4 7h16M10 11v6M14 11v6M6 7l1 13h10l1-13M9 7V4h6v3"/>',
    plus: '<path d="M12 5v14M5 12h14"/>',
    search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>'
};
const ic = (n, s = 18) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">${IC[n]}</svg>`;
$("#logo").innerHTML = ic("check", 17); $("#sico").innerHTML = ic("search", 17);
$("#new").innerHTML = ic("plus", 16) + "Nova tarefa";

const esc = s => String(s).replace(/[&<>"']/g, c => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));
const pd = s => { const [a, b, c] = s.split("-").map(Number); return new Date(a, b - 1, c); };
const today = () => { const d = new Date(); return new Date(d.getFullYear(), d.getMonth(), d.getDate()); };
const iso = d => `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
const fmt = s => { const d = pd(s); return `${d.getDate()} ${MES[d.getMonth()]} ${d.getFullYear()}`; };
const late = t => t.status === "ABERTA" && pd(t.dataVencimento) < today();
function rel(t) {
    if (t.status === "CONCLUIDA") return "Concluída" + (t.dataAtualizacao ? " em " + fmt(t.dataAtualizacao.slice(0, 10)) : "");
    const n = Math.round((pd(t.dataVencimento) - today()) / 864e5);
    if (n === 0) return "Vence hoje";
    if (n === 1) return "Vence amanhã";
    if (n > 1) return `Vence em ${n} dias`;
    return n === -1 ? "Venceu ontem" : `Venceu há ${-n} dias`;
}

async function api(path, opt = {}) {
    let r;
    try {
        r = await fetch(API + path, {method: opt.method || "GET", headers: opt.body ? {"Content-Type": "application/json"} : {}, body: opt.body ? JSON.stringify(opt.body) : undefined});
    } catch {
        setConn(false);
        throw {mensagem: "Não foi possível conectar à API. Confira se o back-end está rodando em localhost:8080."};
    }
    setConn(true);
    if (r.status === 204) return null;
    let data = null;
    try { data = await r.json(); } catch {}
    if (!r.ok) {
        const e = data || {};
        if (!e.mensagem) e.mensagem = r.status === 400 ? "Dados inválidos. Confira os campos e o formato da data." : "Erro inesperado no servidor. Tente novamente.";
        throw e;
    }
    return data;
}
function setConn(ok) {
    const c = $("#conn"); c.className = "conn " + (ok ? "on" : "off");
    c.querySelector("span").textContent = ok ? "API conectada" : "API fora do ar";
}
let tt;
function toast(msg, bad) {
    const t = $("#toast"); t.textContent = msg; t.className = "show" + (bad ? " bad" : "");
    clearTimeout(tt); tt = setTimeout(() => t.className = "", 3500);
}

async function load() {
    S.loading = true; render();
    try { S.all = await api(`?q=${encodeURIComponent(S.q)}&size=1000`); }
    catch (e) { S.all = []; toast(e.mensagem, true); }
    S.loading = false; render();
}

const FILTERS = [["todas", "Todas"], ["abertas", "Abertas"], ["atrasadas", "Atrasadas"], ["concluidas", "Concluídas"]];
const match = {todas: () => true, abertas: t => t.status === "ABERTA", atrasadas: late, concluidas: t => t.status === "CONCLUIDA"};
function visible() {
    const l = S.all.filter(match[S.filter]);
    const by = {venc: (a, b) => a.dataVencimento.localeCompare(b.dataVencimento), nome: (a, b) => a.nome.localeCompare(b.nome, "pt-BR"), novas: (a, b) => b.dataCriacao.localeCompare(a.dataCriacao)};
    return l.sort(by[S.sort]);
}

function render() {
    $("#nav").innerHTML = FILTERS.map(([k, n]) => `<button data-f="${k}" aria-current="${S.filter === k}">${n}<small>${S.all.filter(match[k]).length}</small></button>`).join("");
    const name = FILTERS.find(f => f[0] === S.filter)[1];
    $("#title").textContent = S.filter === "todas" ? "Todas as tarefas" : name;
    const list = $("#list"), foot = $("#foot");
    if (S.loading) { list.innerHTML = '<div class="sk"></div>'.repeat(4); foot.hidden = true; $("#sub").textContent = ""; return; }
    const v = visible(), pages = Math.max(1, Math.ceil(v.length / PER));
    S.page = Math.min(S.page, pages);
    $("#sub").textContent = v.length + (v.length === 1 ? " tarefa" : " tarefas") + (S.q ? ` para “${S.q}”` : "");
    if (!v.length) {
        list.innerHTML = `<div class="empty"><b>${S.q ? "Nada encontrado" : "Nenhuma tarefa por aqui"}</b>${S.q ? "Tente outro termo de busca." : S.filter === "todas" ? "Crie sua primeira tarefa no botão acima." : "Nenhuma tarefa se encaixa neste filtro."}</div>`;
        foot.hidden = true; return;
    }
    list.innerHTML = v.slice((S.page - 1) * PER, S.page * PER).map(t => {
        const done = t.status === "CONCLUIDA", lt = late(t), d = pd(t.dataVencimento), act = done ? "Reabrir" : "Concluir";
        return `<li class="row ${done ? "done" : ""} ${lt ? "late" : ""}" data-id="${t.id}">
      <button class="tick" data-a="toggle" title="${act}" aria-label="${act} tarefa ${esc(t.nome)}">${ic("check", 15)}</button>
      <div><div class="nm">${esc(t.nome)}</div><div class="meta">${rel(t)}</div></div>
      <div class="date" title="Vencimento"><b>${d.getDate()}</b><span>${MES[d.getMonth()]} ${String(d.getFullYear()).slice(2)}</span></div>
      <span class="chip ${done ? "ok" : lt ? "late" : ""}">${done ? "Concluída" : lt ? "Atrasada" : "Aberta"}</span>
      <div class="acts"><button class="ico" data-a="edit" title="Editar" aria-label="Editar ${esc(t.nome)}">${ic("edit")}</button><button class="ico" data-a="del" title="Excluir" aria-label="Excluir ${esc(t.nome)}">${ic("trash")}</button></div></li>`;
    }).join("");
    foot.hidden = pages < 2;
    foot.innerHTML = `<span>Página ${S.page} de ${pages}</span><div><button class="btn" data-p="-1" ${S.page <= 1 ? "disabled" : ""}>Anterior</button><button class="btn" data-p="1" ${S.page >= pages ? "disabled" : ""}>Próxima</button></div>`;
}

// ---- eventos
$("#nav").onclick = e => { const b = e.target.closest("[data-f]"); if (b) { S.filter = b.dataset.f; S.page = 1; render(); } };
$("#foot").onclick = e => { const b = e.target.closest("[data-p]"); if (b) { S.page += +b.dataset.p; render(); scrollTo({top: 0}); } };
$("#sort").onchange = e => { S.sort = e.target.value; render(); };
let deb;
$("#q").oninput = e => { clearTimeout(deb); deb = setTimeout(() => { S.q = e.target.value.trim(); S.page = 1; load(); }, 280); };
addEventListener("keydown", e => { if (e.key === "/" && !/INPUT|SELECT/.test(document.activeElement.tagName) && !document.querySelector("dialog[open]")) { e.preventDefault(); $("#q").focus(); } });

$("#list").onclick = async e => {
    const b = e.target.closest("[data-a]"); if (!b) return;
    const t = S.all.find(x => x.id == b.closest("li").dataset.id);
    if (b.dataset.a === "edit") openForm(t);
    if (b.dataset.a === "del") { S.deleting = t; $("#ctext").textContent = `“${t.nome}” será removida de forma permanente.`; $("#cdlg").showModal(); }
    if (b.dataset.a === "toggle") {
        const done = t.status === "CONCLUIDA";
        try { await api(`/${t.id}/${done ? "reabrir" : "concluir"}`, {method: "PATCH"}); toast(done ? "Tarefa reaberta" : "Tarefa concluída"); load(); }
        catch (er) { toast(er.mensagem, true); }
    }
};

// ---- formulário
const f = $("#f"), dlg = $("#dlg");
function clearErr() { ["nome", "data"].forEach(k => { $("#e-" + k).textContent = ""; $("#" + k).classList.remove("bad"); }); }
function showErr(k, m) { $("#e-" + k).textContent = m; $("#" + k).classList.add("bad"); }
function openForm(t) {
    S.editing = t || null; clearErr();
    $("#dt").textContent = t ? "Editar tarefa" : "Nova tarefa";
    $("#nome").value = t ? t.nome : "";
    $("#data").value = t ? t.dataVencimento : "";
    $("#data").min = t ? t.dataVencimento : iso(today());
    $("#cnt").textContent = $("#nome").value.length + "/60";
    dlg.showModal(); $("#nome").focus();
}
$("#new").onclick = () => openForm();
$("#nome").oninput = e => $("#cnt").textContent = e.target.value.length + "/60";
$("#cancel").onclick = () => dlg.close();
f.onsubmit = async e => {
    e.preventDefault(); clearErr();
    const body = {nome: $("#nome").value.trim(), dataVencimento: $("#data").value};
    let bad = false;
    if (!body.nome) { showErr("nome", "Informe o nome da tarefa."); bad = true; }
    if (!body.dataVencimento) { showErr("data", "Informe a data de vencimento."); bad = true; }
    else if (body.dataVencimento < $("#data").min) { showErr("data", S.editing ? "A data não pode ser anterior ao vencimento atual." : "A data não pode estar no passado."); bad = true; }
    if (bad) return;
    $("#save").disabled = true;
    try {
        const edit = S.editing;
        await (edit ? api("/" + edit.id, {method: "PUT", body}) : api("", {method: "POST", body}));
        dlg.close(); toast(edit ? "Tarefa atualizada" : "Tarefa criada"); load();
    } catch (er) {
        if (er.campos) { if (er.campos.nome) showErr("nome", er.campos.nome); if (er.campos.dataVencimento) showErr("data", er.campos.dataVencimento); }
        else toast(er.mensagem, true);
    }
    $("#save").disabled = false;
};

// ---- exclusão
$("#ccancel").onclick = () => $("#cdlg").close();
$("#cok").onclick = async () => {
    try { await api("/" + S.deleting.id, {method: "DELETE"}); toast("Tarefa excluída"); load(); }
    catch (er) { toast(er.mensagem, true); }
    $("#cdlg").close();
};
document.querySelectorAll("dialog").forEach(d => d.addEventListener("click", e => { if (e.target === d) d.close(); }));

load();