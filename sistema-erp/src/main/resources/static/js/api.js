/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: api.js
 * RUTA: src/main/resources/static/js/api.js
 *
 * QUÉ HACE:
 * Cliente HTTP unificado basado en la Fetch API nativa del navegador.
 *
 * POR QUÉ EXISTE:
 * Centraliza la inyección del token JWT en el encabezado Authorization, procesa respuestas en formato ApiResponse,
 * captura errores 401 (redirección a login) y notifica excepciones mediante el sistema de Toasts.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es importado por auth.js, usuarios.js, roles.js y los futuros módulos de Facturación, Inventario, etc.
 */

const API = {
    baseUrl: '/api',

    /**
     * Realiza una petición HTTP contra los endpoints del backend.
     * @param {string} endpoint - Ruta relativa (ej: '/usuarios' o '/auth/login')
     * @param {object} options - Opciones de fetch (method, body, headers)
     * @returns {Promise<any>} - Carga útil desempaquetada (datos del ApiResponse)
     */
    async request(endpoint, options = {}) {
        const url = `${this.baseUrl}${endpoint}`;
        const headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            ...options.headers
        };

        const token = Auth.getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        try {
            const response = await fetch(url, {
                ...options,
                headers
            });

            // Manejo de expiración de sesión o no autorizado
            if (response.status === 401) {
                Auth.cerrarSesion();
                UI.showToast('Su sesión ha expirado o las credenciales son inválidas', 'error');
                throw new Error('No autorizado');
            }

            if (response.status === 403) {
                UI.showToast('Acceso denegado: No tiene permisos para esta acción', 'error');
                throw new Error('Permisos insuficientes');
            }

            const data = await response.json();

            if (!response.ok || !data.exito) {
                const mensajeError = data.mensaje || 'Error al procesar la solicitud';
                UI.showToast(mensajeError, 'error');
                throw new Error(mensajeError);
            }

            return data.datos;
        } catch (error) {
            console.error(`[API Error] ${endpoint}:`, error);
            throw error;
        }
    },

    get(endpoint) {
        return this.request(endpoint, { method: 'GET' });
    },

    post(endpoint, body) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(body)
        });
    },

    put(endpoint, body) {
        return this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(body)
        });
    },

    patch(endpoint, body) {
        return this.request(endpoint, {
            method: 'PATCH',
            body: JSON.stringify(body)
        });
    },

    delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    }
};

window.API = API;
