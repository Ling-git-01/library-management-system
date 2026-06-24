/**
 * 图书馆管理后台 - 公共脚本
 */

// ---- Token / 角色管理 ----
function getToken() {
    return localStorage.getItem('admin_token') || '';
}

function getRole() {
    return localStorage.getItem('admin_role') || '';
}

// ---- 弹窗 ----
function closeModal() {
    const overlay = document.getElementById('modalOverlay');
    if (overlay) overlay.style.display = 'none';
}

// ---- 退出登录 ----
function logout() {
    // 标记已登出
    sessionStorage.setItem('justLoggedOut', '1');
    // 清除所有 admin 相关 key + 登录页用的 token/user key（防止 login.html 自动跳回）
    Object.keys(localStorage).forEach(function(key) {
        if (key.startsWith('admin_') || key === 'token' || key === 'user') {
            localStorage.removeItem(key);
        }
    });
    // 用 replace 跳转，彻底替换历史记录
    window.location.replace('/api/login.html');
}

// ---- 渲染右上角用户栏 ----
function renderTopbar() {
    const username = localStorage.getItem('admin_username') || localStorage.getItem('admin_role') || '管理员';
    const bar = document.createElement('div');
    bar.className = 'topbar';
    bar.innerHTML =
        '<span class="topbar-username">' + escapeHtml(username) + '</span>' +
        '<button class="btn-logout" onclick="logout()">登出</button>';
    document.body.appendChild(bar);
}

// 简单 HTML 转义，防止用户名里含特殊字符
function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g, c => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    }[c]));
}

// ---- 路由守卫：每个 admin 页面加载时强制校验 ----
(function guardAdmin() {
    // 如果刚登出（sessionStorage 有标记），说明是从 login.html 后进来的，直接放行
    if (sessionStorage.getItem('justLoggedOut') === '1') {
        sessionStorage.removeItem('justLoggedOut');
        return; // 不跳转，让 login.html 正常显示
    }
    const token = getToken();
    const role = getRole();
    if (!token || role !== 'admin') {
        // 未登录或角色非 admin，强制跳到登录页
        const returnTo = encodeURIComponent(location.pathname);
        window.location.replace('/api/login.html?return=' + returnTo);
    }
})();

// 弹窗只通过右上角 ✕ 按钮关闭（不再支持点击遮罩空白处关闭）
document.addEventListener('DOMContentLoaded', () => {
    // 渲染右上角用户栏
    renderTopbar();

    const overlay = document.getElementById('modalOverlay');
    if (!overlay) return;
    const closeBtn = overlay.querySelector('.modal-close');
    if (closeBtn) {
        closeBtn.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();
            closeModal();
        });
    }
    // 点击遮罩不再关闭，避免误操作
    overlay.addEventListener('click', (e) => {
        e.stopPropagation();
    });
});
