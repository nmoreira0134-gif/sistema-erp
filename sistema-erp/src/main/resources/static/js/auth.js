/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: auth.js
 * RUTA: src/main/resources/static/js/auth.js
 *
 * QUÉ HACE:
 * Gestiona el ciclo de vida de la sesión de usuario (almacenamiento del token JWT en sessionStorage,
 * validación de inicio de sesión, extracción de datos de usuario y logout).
 *
 * POR QUÉ EXISTE:
 * Coordina la autenticación en el cliente y garantiza que el usuario cuente con una sesión activa
 * antes de desplegar la interfaz del sistema ERP.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es utilizado por api.js para obtener el token Bearer y por app.js para alternar entre la pantalla
 * de login y el layout del dashboard.
 */

const Auth = {
    TOKEN_KEY: 'erp_jwt_token',
    USER_KEY: 'erp_user_data',

    getToken() {
        return sessionStorage.getItem(this.TOKEN_KEY);
    },

    getUsuario() {
        const userJson = sessionStorage.getItem(this.USER_KEY);
        return userJson ? JSON.parse(userJson) : null;
    },

    estaAutenticado() {
        return !!this.getToken();
    },

    guardarSesion(loginResponse) {
        sessionStorage.setItem(this.TOKEN_KEY, loginResponse.token);
        sessionStorage.setItem(this.USER_KEY, JSON.stringify({
            usuarioId: loginResponse.usuarioId,
            username: loginResponse.username,
            nombreCompleto: loginResponse.nombreCompleto,
            email: loginResponse.email,
            rol: loginResponse.rol,
            permisos: loginResponse.permisos,
            menu: loginResponse.menu
        }));
    },

    cerrarSesion() {
        sessionStorage.removeItem(this.TOKEN_KEY);
        sessionStorage.removeItem(this.USER_KEY);
        window.location.reload();
    },

    async iniciarSesion(username, password) {
        try {
            const data = await API.post('/auth/login', { username, password });
            this.guardarSesion(data);
            UI.showToast(`Bienvenido al ERP, ${data.nombreCompleto}`, 'success');
            App.iniciarDashboard();
        } catch (error) {
            // El error ya es mostrado en UI.showToast por el interceptor API
        }
    }
};

window.Auth = Auth;
