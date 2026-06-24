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
    localStorage.removeItem('admin_token');
    localStorage.removeItem('admin_role');
    localStorage.removeItem('admin_userId');
    window.location.href = '/api/login.html';
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
