/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: usuarios.js
 * RUTA: src/main/resources/static/js/usuarios.js
 *
 * QUÉ HACE:
 * Módulo de frontend en JavaScript Vanilla para la administración de usuarios del sistema ERP.
 *
 * POR QUÉ EXISTE:
 * Provee la interfaz interactiva para listar usuarios registrados, dar de alta nuevos usuarios con
 * roles asignados, editar información personal/contraseñas y alternar el estado activo/inactivo (soft delete).
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Realiza peticiones a los endpoints REST /api/usuarios y /api/roles a través de api.js
 * y se renderiza en el contenedor central #app-content cuando se selecciona la pantalla SEG_USUARIOS.
 */

const UsuariosView = {
    rolesCache: [],

    async render() {
        const content = document.getElementById('app-content');
        if (!content) return;

        content.innerHTML = `
            <div class="card">
                <div class="card-header">
                    <div>
                        <h2 class="card-title">Gestión de Usuarios</h2>
                        <p style="font-size: 0.82rem; color: var(--text-secondary); margin-top: 2px;">
                            Administración de cuentas, perfiles de seguridad y asignación de roles.
                        </p>
                    </div>
                    <div>
                        <button class="btn btn-primary" id="btn-nuevo-usuario">
                            + Nuevo Usuario
                        </button>
                    </div>
                </div>
                <div class="card-body">
                    <div style="margin-bottom: 1rem; display: flex; gap: 1rem; align-items: center;">
                        <input type="text" id="filtro-usuarios" class="form-control" style="max-width: 320px;" 
                               placeholder="Buscar por usuario, nombre o correo...">
                    </div>
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Usuario</th>
                                    <th>Nombre Completo</th>
                                    <th>Email</th>
                                    <th>Rol</th>
                                    <th>Estado</th>
                                    <th>Creado</th>
                                    <th style="text-align: right;">Acciones</th>
                                </tr>
                            </thead>
                            <tbody id="tabla-usuarios-body">
                                <tr><td colspan="8" style="text-align: center; padding: 2rem;">Cargando usuarios...</td></tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        `;

        document.getElementById('btn-nuevo-usuario').addEventListener('click', () => this.abrirModalNuevo());
        document.getElementById('filtro-usuarios').addEventListener('input', (e) => this.filtrarUsuarios(e.target.value));

        await this.cargarRoles();
        await this.cargarUsuarios();
    },

    async cargarRoles() {
        try {
            this.rolesCache = await API.get('/roles');
        } catch (error) {
            this.rolesCache = [];
        }
    },

    async cargarUsuarios() {
        try {
            const usuarios = await API.get('/usuarios');
            this.usuariosData = usuarios;
            this.renderFilas(usuarios);
        } catch (error) {
            document.getElementById('tabla-usuarios-body').innerHTML = `
                <tr><td colspan="8" style="text-align: center; color: var(--danger);">Error al cargar los usuarios.</td></tr>
            `;
        }
    },

    renderFilas(usuarios) {
        const tbody = document.getElementById('tabla-usuarios-body');
        if (!tbody) return;

        if (!usuarios || usuarios.length === 0) {
            tbody.innerHTML = `<tr><td colspan="8" style="text-align: center; padding: 2rem; color: #64748b;">No hay usuarios registrados</td></tr>`;
            return;
        }

        tbody.innerHTML = usuarios.map(u => `
            <tr>
                <td><strong>#${u.usuarioId}</strong></td>
                <td><code>${u.username}</code></td>
                <td>${u.nombreCompleto}</td>
                <td>${u.email}</td>
                <td><span class="badge badge-info">${u.rolNombre}</span></td>
                <td>
                    <span class="badge ${u.activo ? 'badge-success' : 'badge-danger'}">
                        ${u.activo ? 'Activo' : 'Inactivo'}
                    </span>
                </td>
                <td style="font-size: 0.78rem; color: #64748b;">${new Date(u.fechaCreacion).toLocaleDateString()}</td>
                <td style="text-align: right; white-space: nowrap;">
                    <button class="btn btn-secondary btn-sm" onclick="UsuariosView.abrirModalEditar(${u.usuarioId})">Editar</button>
                    <button class="btn ${u.activo ? 'btn-danger' : 'btn-primary'} btn-sm" 
                            onclick="UsuariosView.cambiarEstado(${u.usuarioId}, ${!u.activo})">
                        ${u.activo ? 'Desactivar' : 'Activar'}
                    </button>
                </td>
            </tr>
        `).join('');
    },

    filtrarUsuarios(termino) {
        const query = (termino || '').toLowerCase();
        if (!this.usuariosData) return;
        const filtrados = this.usuariosData.filter(u =>
            u.username.toLowerCase().includes(query) ||
            u.nombreCompleto.toLowerCase().includes(query) ||
            u.email.toLowerCase().includes(query) ||
            u.rolNombre.toLowerCase().includes(query)
        );
        this.renderFilas(filtrados);
    },

    abrirModalNuevo() {
        const opcionesRoles = this.rolesCache.map(r => `
            <option value="${r.rolId}">${r.nombre} - ${r.descripcion || ''}</option>
        `).join('');

        UI.showModal('Registrar Nuevo Usuario', `
            <form id="form-crear-usuario">
                <div class="form-group">
                    <label class="form-label">Nombre de Usuario *</label>
                    <input type="text" id="u-username" class="form-control" required placeholder="ej: jgonzalez">
                </div>
                <div class="form-group">
                    <label class="form-label">Contraseña Temporal *</label>
                    <input type="password" id="u-password" class="form-control" required placeholder="Mínimo 6 caracteres">
                </div>
                <div class="form-group">
                    <label class="form-label">Nombre Completo *</label>
                    <input type="text" id="u-nombre" class="form-control" required placeholder="ej: Juan Alberto González">
                </div>
                <div class="form-group">
                    <label class="form-label">Correo Electrónico *</label>
                    <input type="email" id="u-email" class="form-control" required placeholder="usuario@empresa.com.ni">
                </div>
                <div class="form-group">
                    <label class="form-label">Teléfono</label>
                    <input type="text" id="u-telefono" class="form-control" placeholder="+505 8888-8888">
                </div>
                <div class="form-group">
                    <label class="form-label">Rol de Seguridad *</label>
                    <select id="u-rol" class="form-control" required>
                        <option value="">Seleccione un Rol...</option>
                        ${opcionesRoles}
                    </select>
                </div>
            </form>
        `, async () => {
            const username = document.getElementById('u-username').value.trim();
            const password = document.getElementById('u-password').value;
            const nombreCompleto = document.getElementById('u-nombre').value.trim();
            const email = document.getElementById('u-email').value.trim();
            const telefono = document.getElementById('u-telefono').value.trim();
            const rolId = parseInt(document.getElementById('u-rol').value, 10);

            if (!username || !password || !nombreCompleto || !email || !rolId) {
                UI.showToast('Por favor complete los campos obligatorios', 'error');
                return false;
            }

            try {
                await API.post('/usuarios', {
                    username, password, nombreCompleto, email, telefono, rolId
                });
                UI.showToast('Usuario creado exitosamente', 'success');
                await this.cargarUsuarios();
                return true;
            } catch (error) {
                return false;
            }
        });
    },

    async abrirModalEditar(usuarioId) {
        const usuario = this.usuariosData.find(u => u.usuarioId === usuarioId);
        if (!usuario) return;

        const opcionesRoles = this.rolesCache.map(r => `
            <option value="${r.rolId}" ${r.rolId === usuario.rolId ? 'selected' : ''}>
                ${r.nombre}
            </option>
        `).join('');

        UI.showModal(`Editar Usuario: ${usuario.username}`, `
            <form id="form-editar-usuario">
                <div class="form-group">
                    <label class="form-label">Nombre Completo *</label>
                    <input type="text" id="edit-nombre" class="form-control" value="${usuario.nombreCompleto}" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Correo Electrónico *</label>
                    <input type="email" id="edit-email" class="form-control" value="${usuario.email}" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Teléfono</label>
                    <input type="text" id="edit-telefono" class="form-control" value="${usuario.telefono || ''}">
                </div>
                <div class="form-group">
                    <label class="form-label">Rol de Seguridad *</label>
                    <select id="edit-rol" class="form-control" required>
                        ${opcionesRoles}
                    </select>
                </div>
                <div class="form-group">
                    <label class="form-label">Nueva Contraseña (Opcional)</label>
                    <input type="password" id="edit-password" class="form-control" placeholder="Dejar en blanco para conservar actual">
                </div>
            </form>
        `, async () => {
            const nombreCompleto = document.getElementById('edit-nombre').value.trim();
            const email = document.getElementById('edit-email').value.trim();
            const telefono = document.getElementById('edit-telefono').value.trim();
            const rolId = parseInt(document.getElementById('edit-rol').value, 10);
            const password = document.getElementById('edit-password').value;

            if (!nombreCompleto || !email || !rolId) {
                UI.showToast('Por favor complete los campos requeridos', 'error');
                return false;
            }

            try {
                await API.put(`/usuarios/${usuarioId}`, {
                    nombreCompleto, email, telefono, rolId, password
                });
                UI.showToast('Usuario actualizado con éxito', 'success');
                await this.cargarUsuarios();
                return true;
            } catch (error) {
                return false;
            }
        });
    },

    async cambiarEstado(usuarioId, nuevoEstado) {
        const accion = nuevoEstado ? 'activar' : 'desactivar';
        if (!confirm(`¿Está seguro de que desea ${accion} este usuario?`)) return;

        try {
            await API.patch(`/usuarios/${usuarioId}/estado`, { activo: nuevoEstado });
            UI.showToast(`Estado de usuario actualizado`, 'success');
            await this.cargarUsuarios();
        } catch (error) {
            // El error es mostrado por API wrapper
        }
    }
};

window.UsuariosView = UsuariosView;
