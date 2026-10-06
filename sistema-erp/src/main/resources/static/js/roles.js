/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: roles.js
 * RUTA: src/main/resources/static/js/roles.js
 *
 * QUÉ HACE:
 * Módulo de frontend en JavaScript Vanilla para consultar roles y visualizar la matriz de permisos
 * por pantalla y operación del sistema ERP.
 *
 * POR QUÉ EXISTE:
 * Permite auditar qué pantallas y qué acciones (ACCESO, CREAR, EDITAR, ELIMINAR, ANULAR, IMPRIMIR)
 * tiene habilitadas cada rol (Administrador, Gerente, Vendedor, Contador, RH, Consulta).
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Realiza peticiones a /api/roles y /api/roles/{id}/matriz a través de api.js
 * y se renderiza en #app-content al seleccionar la pantalla SEG_ROLES.
 */

const RolesView = {
    rolesList: [],
    rolSeleccionadoId: null,

    async render() {
        const content = document.getElementById('app-content');
        if (!content) return;

        content.innerHTML = `
            <div class="card">
                <div class="card-header">
                    <div>
                        <h2 class="card-title">Roles y Matriz de Autorizaciones</h2>
                        <p style="font-size: 0.82rem; color: var(--text-secondary); margin-top: 2px;">
                            Inspección y auditoría de permisos asignados a cada perfil de usuario.
                        </p>
                    </div>
                </div>
                <div class="card-body">
                    <div style="display: flex; gap: 1rem; margin-bottom: 1.5rem; align-items: center; flex-wrap: wrap;">
                        <label class="form-label" style="margin: 0; font-weight: 700;">Seleccionar Rol:</label>
                        <select id="select-rol-matriz" class="form-control" style="max-width: 280px;">
                            <option value="">Cargando roles...</option>
                        </select>
                        <span id="rol-desc-badge" style="font-size: 0.85rem; color: #475569;"></span>
                    </div>

                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Módulo</th>
                                    <th>Código</th>
                                    <th>Pantalla</th>
                                    <th style="text-align: center;">Acceso</th>
                                    <th style="text-align: center;">Crear</th>
                                    <th style="text-align: center;">Editar</th>
                                    <th style="text-align: center;">Eliminar</th>
                                    <th style="text-align: center;">Anular</th>
                                    <th style="text-align: center;">Imprimir</th>
                                </tr>
                            </thead>
                            <tbody id="tabla-matriz-body">
                                <tr><td colspan="9" style="text-align: center; padding: 2rem;">Seleccione un rol para consultar la matriz</td></tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        `;

        await this.cargarRoles();
    },

    async cargarRoles() {
        try {
            this.rolesList = await API.get('/roles');
            const select = document.getElementById('select-rol-matriz');
            if (!select) return;

            select.innerHTML = this.rolesList.map(r => `
                <option value="${r.rolId}">${r.nombre}</option>
            `).join('');

            select.addEventListener('change', (e) => {
                this.seleccionarRol(parseInt(e.target.value, 10));
            });

            if (this.rolesList.length > 0) {
                this.seleccionarRol(this.rolesList[0].rolId);
            }
        } catch (error) {
            UI.showToast('Error al cargar la lista de roles', 'error');
        }
    },

    async seleccionarRol(rolId) {
        this.rolSeleccionadoId = rolId;
        const rol = this.rolesList.find(r => r.rolId === rolId);
        const descSpan = document.getElementById('rol-desc-badge');
        if (descSpan && rol) {
            descSpan.textContent = rol.descripcion || '';
        }

        const tbody = document.getElementById('tabla-matriz-body');
        if (tbody) {
            tbody.innerHTML = `<tr><td colspan="9" style="text-align: center; padding: 2rem;">Cargando permisos del rol...</td></tr>`;
        }

        try {
            const matriz = await API.get(`/roles/${rolId}/matriz`);
            this.renderMatriz(matriz);
        } catch (error) {
            if (tbody) {
                tbody.innerHTML = `<tr><td colspan="9" style="text-align: center; color: var(--danger);">Error al cargar matriz de permisos</td></tr>`;
            }
        }
    },

    renderMatriz(matriz) {
        const tbody = document.getElementById('tabla-matriz-body');
        if (!tbody) return;

        const checkIcon = '<span style="color: #059669; font-weight: bold; font-size: 1.1rem;">✔</span>';
        const crossIcon = '<span style="color: #cbd5e1; font-size: 1rem;">—</span>';

        tbody.innerHTML = matriz.map(item => {
            const tiene = (perm) => item.permisosAsignados.includes(perm);

            return `
                <tr>
                    <td><strong>${item.modulo}</strong></td>
                    <td><code>${item.codigoPantalla}</code></td>
                    <td>${item.nombrePantalla}</td>
                    <td style="text-align: center;">${tiene('ACCESO') ? checkIcon : crossIcon}</td>
                    <td style="text-align: center;">${tiene('CREAR') ? checkIcon : crossIcon}</td>
                    <td style="text-align: center;">${tiene('EDITAR') ? checkIcon : crossIcon}</td>
                    <td style="text-align: center;">${tiene('ELIMINAR') ? checkIcon : crossIcon}</td>
                    <td style="text-align: center;">${tiene('ANULAR') ? checkIcon : crossIcon}</td>
                    <td style="text-align: center;">${tiene('IMPRIMIR') ? checkIcon : crossIcon}</td>
                </tr>
            `;
        }).join('');
    }
};

window.RolesView = RolesView;
