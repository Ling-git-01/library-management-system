/**
 * 图书馆管理后台 - 公共脚本
 */

// ---- Token 管理 ----
function getToken() {
    return localStorage.getItem('admin_token') || '';
}

// ---- 弹窗 ----
function closeModal() {
    document.getElementById('modalOverlay').style.display = 'none';
}

// 点击遮罩关闭弹窗
document.addEventListener('DOMContentLoaded', () => {
    const overlay = document.getElementById('modalOverlay');
    if (overlay) {
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) closeModal();
        });
    }
});

// ---- 退出登录 ----
function logout() {
    localStorage.removeItem('admin_token');
    window.location.href = '/login.html';
}
