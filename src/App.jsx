import { useEffect, useState } from "react";

export default function App() {
  const [form, setForm] = useState({
    serviceType: "NAIL",
    customerName: "",
    phone: "",
    startTime: "",        // 例如 2025-09-05T10:30
    durationMinutes: 60,
    remark: ""
  });

  const [date, setDate] = useState(() => new Date().toISOString().slice(0, 10));
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState("");

  async function fetchList(d) {
    setLoading(true);
    try {
      const res = await fetch(`/api/appointments?date=${d}`);
      if (!res.ok) throw new Error(await res.text());
      setList(await res.json());
    } catch (e) {
      setMsg("查询失败：" + e.message);
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => { fetchList(date); }, [date]);

  async function onSubmit(e) {
    e.preventDefault();
    setMsg("");
    const start = form.startTime?.length === 16 ? form.startTime + ":00" : form.startTime;
    try {
      const res = await fetch(`/api/appointments`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ ...form, startTime: start, durationMinutes: Number(form.durationMinutes) })
      });
      if (!res.ok) throw new Error(await res.text());
      setMsg("已添加 ✅");
      const day = start.slice(0, 10) || date;
      setDate(day);
      await fetchList(day);
    } catch (e) {
      setMsg("添加失败：" + e.message);
    }
  }

  const onChange = (k) => (e) => setForm(f => ({ ...f, [k]: e.target.value }));

  return (
    <div style={{ maxWidth: 760, margin: "32px auto", padding: 16, fontFamily: "system-ui, -apple-system, Segoe UI, Roboto" }}>
      <h2 style={{ marginBottom: 12 }}>美甲预约（技师端）</h2>

      <form onSubmit={onSubmit} style={{ display: "grid", gap: 10, marginBottom: 16 }}>
        <label>服务：
          <select value={form.serviceType} onChange={onChange("serviceType")}>
            <option value="NAIL">美甲</option>
            <option value="LASH">美睫</option>
            <option value="BROW">纹眉</option>
          </select>
        </label>

        <label>客户姓名：
          <input value={form.customerName} onChange={onChange("customerName")} required />
        </label>

        <label>手机号：
          <input value={form.phone} onChange={onChange("phone")} required />
        </label>

        <label>开始时间：
          <input type="datetime-local" value={form.startTime} onChange={onChange("startTime")} required />
        </label>

        <label>时长（分钟）：
          <input type="number" min="15" step="15" value={form.durationMinutes} onChange={onChange("durationMinutes")} required />
        </label>

        <label>备注：
          <input value={form.remark} onChange={onChange("remark")} />
        </label>

        <button type="submit">添加预约</button>
      </form>

      <div style={{ display: "flex", alignItems: "center", gap: 8, marginBottom: 8 }}>
        <span>查看日期：</span>
        <input type="date" value={date} onChange={(e) => setDate(e.target.value)} />
        {loading && <span>加载中…</span>}
      </div>

      {msg && <div style={{ marginBottom: 8 }}>{msg}</div>}

      <ul style={{ padding: 0, listStyle: "none", display: "grid", gap: 8 }}>
        {list.map(a => (
          <li key={a.id} style={{ border: "1px solid #e5e5e5", borderRadius: 8, padding: 10 }}>
            <div><b>{a.customerName}</b>（{a.serviceType}）</div>
            <div>{a.phone}</div>
            <div>{a.startTime.replace("T", " ")} · {a.durationMinutes} 分钟</div>
            {a.remark && <div>备注：{a.remark}</div>}
          </li>
        ))}
        {!loading && list.length === 0 && <li>该日暂无预约</li>}
      </ul>
    </div>
  );
}
