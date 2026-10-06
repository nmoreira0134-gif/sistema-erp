/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: app.js
 * RUTA: src/main/resources/static/js/app.js
 *
 * QUÉ HACE:
 * Orquestador principal del frontend Vanilla. Inicializa la aplicación, gestiona los modales,
 * toasts, estados de carga y alterna entre la vista de Login y el Dashboard del sistema.
 *
 * POR QUÉ EXISTE:
 * Centraliza la lógica global de la interfaz de usuario sin requerir frameworks SPA como React o Vue.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es el punto de entrada que escucha 'DOMContentLoaded', coordina Auth, Menu, UsuariosView y RolesView.
 */

const UI = {
    showToast(mensaje, tipo = 'info') {
        const container = document.getElementById('toast-container');
        if (!container) return;

        const toast = document.createElement('div');
        toast.className = `toast toast-${tipo}`;
        toast.textContent = mensaje;

        container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transition = 'opacity 0.3s ease';
            setTimeout(() => toast.remove(), 300);
        }, 3500);
    },

    showModal(titulo, htmlContenido, onConfirmCallback) {
        const container = document.getElementById('modal-container');
        if (!container) return;

        container.innerHTML = `
            <div class="modal-overlay" id="active-modal-overlay">
                <div class="modal-card">
                    <div class="modal-header">
                        <h3>${titulo}</h3>
                        <button class="modal-close" onclick="UI.closeModal()">&times;</button>
                    </div>
                    <div class="modal-body">
                        ${htmlContenido}
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" onclick="UI.closeModal()">Cancelar</button>
                        <button class="btn btn-primary" id="modal-btn-confirmar">Guardar</button>
                    </div>
                </div>
            </div>
        `;

        const confirmBtn = document.getElementById('modal-btn-confirmar');
        if (confirmBtn && onConfirmCallback) {
            confirmBtn.addEventListener('click', async () => {
                confirmBtn.disabled = true;
                confirmBtn.textContent = 'Procesando...';
                try {
                    const exito = await onConfirmCallback();
                    if (exito) {
                        UI.closeModal();
                    }
                } finally {
                    confirmBtn.disabled = false;
                    confirmBtn.textContent = 'Guardar';
                }
            });
        }
    },

    closeModal() {
        const container = document.getElementById('modal-container');
        if (container) container.innerHTML = '';
    },

    renderVistaEnConstruccion(codigoPantalla) {
        const content = document.getElementById('app-content');
        if (!content) return;

        content.innerHTML = `
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">Módulo en Desarrollo: <code>${codigoPantalla}</code></h2>
                </div>
                <div class="card-body" style="text-align: center; padding: 3rem 1rem;">
                    <div style="font-size: 2.5rem; margin-bottom: 1rem;">⚙️</div>
                    <h3 style="color: var(--text-primary); margin-bottom: 0.5rem;">Pantalla preparada en la matriz de seguridad</h3>
                    <p style="color: var(--text-secondary); max-width: 500px; margin: 0 auto; font-size: 0.9rem;">
                        Esta opción de menú está debidamente registrada en la base de datos y autorizada para su rol.
                        Su implementación operativa corresponde a las siguientes fases del plan arquitectónico del ERP.
                    </p>
                </div>
            </div>
        `;
    }
};

const App = {
    init() {
        this.vincularEventosGlobales();

        if (Auth.estaAutenticado()) {
            this.iniciarDashboard();
        } else {
            this.mostrarLogin();
        }
    },

    vincularEventosGlobales() {
        // Toggle Sidebar
        const toggleBtn = document.getElementById('btn-toggle-sidebar');
        const sidebar = document.getElementById('sidebar');
        if (toggleBtn && sidebar) {
            toggleBtn.addEventListener('click', () => {
                sidebar.classList.toggle('collapsed');
            });
        }

        // Formulario de Login
        const loginForm = document.getElementById('login-form');
        if (loginForm) {
            loginForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const user = document.getElementById('login-username').value.trim();
                const pass = document.getElementById('login-password').value;
                const submitBtn = document.getElementById('btn-login-submit');

                submitBtn.disabled = true;
                submitBtn.textContent = 'Iniciando sesión...';
                try {
                    await Auth.iniciarSesion(user, pass);
                } finally {
                    submitBtn.disabled = false;
                    submitBtn.textContent = 'Ingresar al ERP';
                }
            });
        }

        // Botón Logout
        const logoutBtn = document.getElementById('btn-logout');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', () => {
                if (confirm('¿Desea cerrar la sesión del ERP?')) {
                    Auth.cerrarSesion();
                }
            });
        }
    },

    mostrarLogin() {
        const loginScreen = document.getElementById('login-screen');
        const appLayout = document.getElementById('app-layout');
        if (loginScreen) loginScreen.style.display = 'flex';
        if (appLayout) appLayout.style.display = 'none';
    },

    iniciarDashboard() {
        const loginScreen = document.getElementById('login-screen');
        const appLayout = document.getElementById('app-layout');
        if (loginScreen) loginScreen.style.display = 'none';
        if (appLayout) appLayout.style.display = 'flex';

        const usuario = Auth.getUsuario();
        if (usuario) {
            const userNameEl = document.getElementById('topbar-user-name');
            const userRoleEl = document.getElementById('topbar-user-role');
            if (userNameEl) userNameEl.textContent = usuario.nombreCompleto;
            if (userRoleEl) userRoleEl.textContent = `${usuario.rol} (@${usuario.username})`;

            Menu.render(usuario.menu);
        }
    }
};

window.UI = UI;
window.App = App;

document.addEventListener('DOMContentLoaded', () => {
    App.init();
});
