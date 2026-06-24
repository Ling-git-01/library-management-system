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
    window.location.href = '/';
}

// ---- 路由守卫：每个 admin 页面加载时强制校验 ----
(function guardAdmin() {
    const token = getToken();
    const role = getRole();
    if (!token || role !== 'admin') {
        // 没有有效管理员身份，跳回登录页
        const returnTo = encodeURIComponent(location.pathname);
        window.location.replace('/api/login.html?return=' + returnTo);
    }
})();

// 弹窗只通过右上角 ✕ 按钮关闭（不再支持点击遮罩空白处关闭）
document.addEventListener('DOMContentLoaded', () => {
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
