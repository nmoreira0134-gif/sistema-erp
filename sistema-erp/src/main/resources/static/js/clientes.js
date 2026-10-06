/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: clientes.js
 * RUTA: src/main/resources/static/js/clientes.js
 *
 * QUÉ HACE:
 * Módulo de interfaz gráfica en JavaScript Vanilla para el catálogo maestro de Clientes.
 *
 * POR QUÉ EXISTE:
 * Provee la experiencia de usuario para listar clientes con paginación y búsqueda en tiempo real,
 * registrar clientes Naturales (con Cédula) o Jurídicos (con RUC y Razón Social), configurar
 * límites de crédito y alternar estados de activación (Soft Delete).
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Consume los endpoints REST de /api/clientes mediante api.js y es montado en el contenedor
 * principal #app-content por menu.js al seleccionar la opción VEN_CLIENTES.
 */

const ClientesView = {
    paginaActual: 0,
    tamanoPagina: 10,
    terminoBusqueda: '',
    filtroTipo: '',
    clientesData: [],
    metadatosPaginacion: null,

    async render() {
        const content = document.getElementById('app-content');
        if (!content) return;

        content.innerHTML = `
            <div class="card">
                <div class="card-header">
                    <div>
                        <h2 class="card-title">Gestión de Clientes</h2>
                        <p style="font-size: 0.82rem; color: var(--text-secondary); margin-top: 2px;">
                            Catálogo maestro comercial, asignación de condiciones de crédito y control fiscal (DGI).
                        </p>
                    </div>
                    <div>
                        <button class="btn btn-primary" id="btn-nuevo-cliente">
                            + Nuevo Cliente
                        </button>
                    </div>
                </div>
                <div class="card-body">
                    <!-- BARRA DE HERRAMIENTAS: BÚSQUEDA Y FILTROS -->
                    <div style="display: flex; gap: 1rem; margin-bottom: 1.25rem; flex-wrap: wrap; align-items: center;">
                        <input type="text" id="filtro-cliente-texto" class="form-control" style="max-width: 320px;" 
                               placeholder="Buscar por código, nombre, documento, correo...">
                        
                        <select id="filtro-cliente-tipo" class="form-control" style="max-width: 180px;">
                            <option value="">Todos los Tipos</option>
                            <option value="Natural">Persona Natural</option>
                            <option value="Juridico">Persona Jurídica</option>
                        </select>

                        <button class="btn btn-secondary btn-sm" id="btn-refrescar-clientes" title="Recargar">
                            ↻ Refrescar
                        </button>
                    </div>

                    <!-- TABLA DE CLIENTES -->
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Código</th>
                                    <th>Nombre / Razón Social</th>
                                    <th>Tipo</th>
                                    <th>Documento Identidad</th>
                                    <th>Contacto</th>
                                    <th>Condición Crédito</th>
                                    <th>Estado</th>
                                    <th style="text-align: right;">Acciones</th>
                                </tr>
                            </thead>
                            <tbody id="tabla-clientes-body">
                                <tr><td colspan="8" style="text-align: center; padding: 2rem;">Cargando catálogo de clientes...</td></tr>
                            </tbody>
                        </table>
                    </div>

                    <!-- BARRA DE PAGINACIÓN -->
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 1.25rem; padding-top: 0.75rem; border-top: 1px solid var(--border-color); flex-wrap: wrap; gap: 0.75rem;">
                        <span id="info-paginacion-clientes" style="font-size: 0.85rem; color: var(--text-secondary);">
                            Mostrando registros
                        </span>
                        <div style="display: flex; gap: 0.5rem;">
                            <button class="btn btn-secondary btn-sm" id="btn-pag-anterior" disabled>◀ Anterior</button>
                            <button class="btn btn-secondary btn-sm" id="btn-pag-siguiente" disabled>Siguiente ▶</button>
                        </div>
                    </div>
                </div>
            </div>
        `;

        this.vincularEventos();
        await this.cargarClientes();
    },

    vincularEventos() {
        document.getElementById('btn-nuevo-cliente').addEventListener('click', () => this.abrirModalNuevo());

        const inputBusqueda = document.getElementById('filtro-cliente-texto');
        let debounceTimer;
        inputBusqueda.addEventListener('input', (e) => {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(() => {
                this.terminoBusqueda = e.target.value.trim();
                this.paginaActual = 0;
                this.cargarClientes();
            }, 300);
        });

        const selectTipo = document.getElementById('filtro-cliente-tipo');
        selectTipo.addEventListener('change', (e) => {
            this.filtroTipo = e.target.value;
            this.paginaActual = 0;
            this.cargarClientes();
        });

        document.getElementById('btn-refrescar-clientes').addEventListener('click', () => {
            this.cargarClientes();
        });

        document.getElementById('btn-pag-anterior').addEventListener('click', () => {
            if (this.paginaActual > 0) {
                this.paginaActual--;
                this.cargarClientes();
            }
        });

        document.getElementById('btn-pag-siguiente').addEventListener('click', () => {
            if (this.metadatosPaginacion && !this.metadatosPaginacion.esUltima) {
                this.paginaActual++;
                this.cargarClientes();
            }
        });
    },

    async cargarClientes() {
        const tbody = document.getElementById('tabla-clientes-body');
        if (tbody) {
            tbody.innerHTML = `<tr><td colspan="8" style="text-align: center; padding: 2rem;">Cargando...</td></tr>`;
        }

        try {
            let endpoint = `/clientes?page=${this.paginaActual}&size=${this.tamanoPagina}&sort=clienteId,desc`;
            if (this.terminoBusqueda) {
                endpoint = `/clientes/buscar?q=${encodeURIComponent(this.terminoBusqueda)}&page=${this.paginaActual}&size=${this.tamanoPagina}&sort=clienteId,desc`;
            }

            const data = await API.get(endpoint);
            this.metadatosPaginacion = data;
            let items = data.contenido || [];

            // Filtro local opcional por tipo si está seleccionado
            if (this.filtroTipo) {
                items = items.filter(c => c.tipoCliente === this.filtroTipo);
            }

            this.clientesData = items;
            this.renderFilas(items);
            this.actualizarPaginacion(data);
        } catch (error) {
            if (tbody) {
                tbody.innerHTML = `<tr><td colspan="8" style="text-align: center; color: var(--danger); padding: 2rem;">Error al recuperar clientes</td></tr>`;
            }
        }
    },

    renderFilas(clientes) {
        const tbody = document.getElementById('tabla-clientes-body');
        if (!tbody) return;

        if (!clientes || clientes.length === 0) {
            tbody.innerHTML = `<tr><td colspan="8" style="text-align: center; padding: 2rem; color: #64748b;">No se encontraron clientes registrados</td></tr>`;
            return;
        }

        tbody.innerHTML = clientes.map(c => {
            const nombreMostrar = c.tipoCliente === 'Juridico' && c.razonSocial
                ? `<strong>${c.razonSocial}</strong><br><span style="font-size: 0.78rem; color: var(--text-secondary);">${c.nombreCompleto} (Contacto)</span>`
                : `<strong>${c.nombreCompleto}</strong>`;

            const creditoBadge = c.esCredito
                ? `<span class="badge badge-info">Crédito: C$ ${Number(c.limiteCredito).toLocaleString('es-NI', { minimumFractionDigits: 2 })} (${c.diasCredito} d)</span>`
                : `<span class="badge" style="background-color: #f1f5f9; color: #475569;">Contado</span>`;

            const tipoBadge = c.tipoCliente === 'Juridico'
                ? `<span class="badge" style="background-color: #ede9fe; color: #6d28d9;">Jurídico</span>`
                : `<span class="badge" style="background-color: #e0f2fe; color: #0369a1;">Natural</span>`;

            return `
                <tr>
                    <td><code>${c.codigoCliente}</code></td>
                    <td>${nombreMostrar}</td>
                    <td>${tipoBadge}</td>
                    <td><code>${c.documentoIdentidad}</code></td>
                    <td>
                        <div style="font-size: 0.82rem;">
                            ${c.telefono ? `📞 ${c.telefono}<br>` : ''}
                            ${c.email ? `✉️ ${c.email}` : ''}
                        </div>
                    </td>
                    <td>${creditoBadge}</td>
                    <td>
                        <span class="badge ${c.activo ? 'badge-success' : 'badge-danger'}">
                            ${c.activo ? 'Activo' : 'Inactivo'}
                        </span>
                    </td>
                    <td style="text-align: right; white-space: nowrap;">
                        <button class="btn btn-secondary btn-sm" onclick="ClientesView.abrirModalEditar(${c.clienteId})">Editar</button>
                        <button class="btn ${c.activo ? 'btn-danger' : 'btn-primary'} btn-sm" 
                                onclick="ClientesView.cambiarEstado(${c.clienteId}, ${!c.activo})">
                            ${c.activo ? 'Desactivar' : 'Activar'}
                        </button>
                    </td>
                </tr>
            `;
        }).join('');
    },

    actualizarPaginacion(meta) {
        const info = document.getElementById('info-paginacion-clientes');
        const btnAnt = document.getElementById('btn-pag-anterior');
        const btnSig = document.getElementById('btn-pag-siguiente');

        if (info) {
            const desde = meta.totalElementos === 0 ? 0 : meta.pagina * meta.elementosPorPagina + 1;
            const hasta = Math.min((meta.pagina + 1) * meta.elementosPorPagina, meta.totalElementos);
            info.textContent = `Mostrando ${desde} - ${hasta} de ${meta.totalElementos} clientes (Página ${meta.pagina + 1} de ${Math.max(meta.totalPaginas, 1)})`;
        }

        if (btnAnt) btnAnt.disabled = meta.esPrimera;
        if (btnSig) btnSig.disabled = meta.esUltima;
    },

    abrirModalNuevo() {
        UI.showModal('Registrar Nuevo Cliente', `
            <form id="form-cliente">
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label">Tipo de Cliente *</label>
                        <select id="c-tipo" class="form-control" required onchange="ClientesView.toggleCamposTipo(this.value)">
                            <option value="Natural">Persona Natural</option>
                            <option value="Juridico">Persona Jurídica (Empresa)</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Código Cliente (Opcional)</label>
                        <input type="text" id="c-codigo" class="form-control" placeholder="Auto: CLI-2026-XXXX">
                    </div>
                </div>

                <div class="form-group" id="grupo-razon-social" style="display: none;">
                    <label class="form-label">Razón Social *</label>
                    <input type="text" id="c-razon-social" class="form-control" placeholder="ej: Distribuidora Central S.A.">
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label" id="label-nombre">Nombre(s) *</label>
                        <input type="text" id="c-nombre" class="form-control" required placeholder="ej: Carlos Alberto">
                    </div>
                    <div class="form-group">
                        <label class="form-label" id="label-apellido">Apellido(s) *</label>
                        <input type="text" id="c-apellido" class="form-control" required placeholder="ej: Mendoza Ruiz">
                    </div>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label" id="label-documento">Cédula de Identidad *</label>
                        <input type="text" id="c-documento" class="form-control" required placeholder="001-280590-0001A">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Teléfono</label>
                        <input type="text" id="c-telefono" class="form-control" placeholder="+505 8888-8888">
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Correo Electrónico</label>
                    <input type="email" id="c-email" class="form-control" placeholder="cliente@dominio.com.ni">
                </div>

                <div class="form-group">
                    <label class="form-label">Dirección</label>
                    <input type="text" id="c-direccion" class="form-control" placeholder="De la Rotonda El Güegüense 2c al lago">
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label">Ciudad</label>
                        <input type="text" id="c-ciudad" class="form-control" placeholder="Managua">
                    </div>
                    <div class="form-group">
                        <label class="form-label">País</label>
                        <input type="text" id="c-pais" class="form-control" value="Nicaragua">
                    </div>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; background-color: #f8fafc; padding: 0.85rem; border-radius: 6px; border: 1px solid var(--border-color);">
                    <div class="form-group" style="margin: 0;">
                        <label class="form-label">Límite de Crédito (C$)</label>
                        <input type="number" step="0.01" min="0" id="c-limite" class="form-control" value="0.00">
                    </div>
                    <div class="form-group" style="margin: 0;">
                        <label class="form-label">Días de Crédito</label>
                        <input type="number" min="0" id="c-dias" class="form-control" value="0">
                    </div>
                </div>
            </form>
        `, async () => {
            const tipo = document.getElementById('c-tipo').value;
            const codigo = document.getElementById('c-codigo').value.trim();
            const nombre = document.getElementById('c-nombre').value.trim();
            const apellido = document.getElementById('c-apellido').value.trim();
            const razonSocial = document.getElementById('c-razon-social').value.trim();
            const documento = document.getElementById('c-documento').value.trim();
            const email = document.getElementById('c-email').value.trim();
            const telefono = document.getElementById('c-telefono').value.trim();
            const direccion = document.getElementById('c-direccion').value.trim();
            const ciudad = document.getElementById('c-ciudad').value.trim();
            const pais = document.getElementById('c-pais').value.trim() || 'Nicaragua';
            const limiteCredito = parseFloat(document.getElementById('c-limite').value || '0');
            const diasCredito = parseInt(document.getElementById('c-dias').value || '0', 10);

            if (!nombre || !apellido || !documento) {
                UI.showToast('Complete los campos obligatorios', 'error');
                return false;
            }

            if (tipo === 'Juridico' && !razonSocial) {
                UI.showToast('La Razón Social es obligatoria para clientes Jurídicos', 'error');
                return false;
            }

            try {
                await API.post('/clientes', {
                    codigoCliente: codigo || null,
                    nombre,
                    apellido,
                    razonSocial: tipo === 'Juridico' ? razonSocial : null,
                    tipoCliente: tipo,
                    documentoIdentidad: documento,
                    email: email || null,
                    telefono: telefono || null,
                    direccion: direccion || null,
                    ciudad: ciudad || null,
                    pais,
                    limiteCredito,
                    diasCredito
                });
                UI.showToast('Cliente registrado exitosamente', 'success');
                await this.cargarClientes();
                return true;
            } catch (error) {
                return false;
            }
        });
    },

    abrirModalEditar(clienteId) {
        const cliente = this.clientesData.find(c => c.clienteId === clienteId);
        if (!cliente) return;

        const esJuridico = cliente.tipoCliente === 'Juridico';

        UI.showModal(`Editar Cliente: ${cliente.codigoCliente}`, `
            <form id="form-cliente-editar">
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label">Tipo de Cliente *</label>
                        <select id="edit-c-tipo" class="form-control" required onchange="ClientesView.toggleCamposTipoEditar(this.value)">
                            <option value="Natural" ${!esJuridico ? 'selected' : ''}>Persona Natural</option>
                            <option value="Juridico" ${esJuridico ? 'selected' : ''}>Persona Jurídica (Empresa)</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Código Cliente (Inmutable)</label>
                        <input type="text" class="form-control" value="${cliente.codigoCliente}" disabled>
                    </div>
                </div>

                <div class="form-group" id="edit-grupo-razon-social" style="${esJuridico ? 'display: block;' : 'display: none;'}">
                    <label class="form-label">Razón Social *</label>
                    <input type="text" id="edit-c-razon-social" class="form-control" value="${cliente.razonSocial || ''}" placeholder="ej: Distribuidora Central S.A.">
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label" id="edit-label-nombre">${esJuridico ? 'Nombre Contacto *' : 'Nombre(s) *'}</label>
                        <input type="text" id="edit-c-nombre" class="form-control" value="${cliente.nombre}" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" id="edit-label-apellido">${esJuridico ? 'Apellido Contacto *' : 'Apellido(s) *'}</label>
                        <input type="text" id="edit-c-apellido" class="form-control" value="${cliente.apellido}" required>
                    </div>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label" id="edit-label-documento">${esJuridico ? 'RUC *' : 'Cédula de Identidad *'}</label>
                        <input type="text" id="edit-c-documento" class="form-control" value="${cliente.documentoIdentidad}" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Teléfono</label>
                        <input type="text" id="edit-c-telefono" class="form-control" value="${cliente.telefono || ''}">
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Correo Electrónico</label>
                    <input type="email" id="edit-c-email" class="form-control" value="${cliente.email || ''}">
                </div>

                <div class="form-group">
                    <label class="form-label">Dirección</label>
                    <input type="text" id="edit-c-direccion" class="form-control" value="${cliente.direccion || ''}">
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label">Ciudad</label>
                        <input type="text" id="edit-c-ciudad" class="form-control" value="${cliente.ciudad || ''}">
                    </div>
                    <div class="form-group">
                        <label class="form-label">País</label>
                        <input type="text" id="edit-c-pais" class="form-control" value="${cliente.pais || 'Nicaragua'}">
                    </div>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; background-color: #f8fafc; padding: 0.85rem; border-radius: 6px; border: 1px solid var(--border-color);">
                    <div class="form-group" style="margin: 0;">
                        <label class="form-label">Límite de Crédito (C$)</label>
                        <input type="number" step="0.01" min="0" id="edit-c-limite" class="form-control" value="${cliente.limiteCredito}">
                    </div>
                    <div class="form-group" style="margin: 0;">
                        <label class="form-label">Días de Crédito</label>
                        <input type="number" min="0" id="edit-c-dias" class="form-control" value="${cliente.diasCredito}">
                    </div>
                </div>
            </form>
        `, async () => {
            const tipo = document.getElementById('edit-c-tipo').value;
            const nombre = document.getElementById('edit-c-nombre').value.trim();
            const apellido = document.getElementById('edit-c-apellido').value.trim();
            const razonSocial = document.getElementById('edit-c-razon-social').value.trim();
            const documento = document.getElementById('edit-c-documento').value.trim();
            const email = document.getElementById('edit-c-email').value.trim();
            const telefono = document.getElementById('edit-c-telefono').value.trim();
            const direccion = document.getElementById('edit-c-direccion').value.trim();
            const ciudad = document.getElementById('edit-c-ciudad').value.trim();
            const pais = document.getElementById('edit-c-pais').value.trim() || 'Nicaragua';
            const limiteCredito = parseFloat(document.getElementById('edit-c-limite').value || '0');
            const diasCredito = parseInt(document.getElementById('edit-c-dias').value || '0', 10);

            if (!nombre || !apellido || !documento) {
                UI.showToast('Complete los campos obligatorios', 'error');
                return false;
            }

            if (tipo === 'Juridico' && !razonSocial) {
                UI.showToast('La Razón Social es obligatoria para clientes Jurídicos', 'error');
                return false;
            }

            try {
                await API.put(`/clientes/${clienteId}`, {
                    nombre,
                    apellido,
                    razonSocial: tipo === 'Juridico' ? razonSocial : null,
                    tipoCliente: tipo,
                    documentoIdentidad: documento,
                    email: email || null,
                    telefono: telefono || null,
                    direccion: direccion || null,
                    ciudad: ciudad || null,
                    pais,
                    limiteCredito,
                    diasCredito
                });
                UI.showToast('Cliente actualizado exitosamente', 'success');
                await this.cargarClientes();
                return true;
            } catch (error) {
                return false;
            }
        });
    },

    toggleCamposTipo(tipo) {
        const grupoRazon = document.getElementById('grupo-razon-social');
        const labelDoc = document.getElementById('label-documento');
        const inputDoc = document.getElementById('c-documento');
        const labelNom = document.getElementById('label-nombre');
        const labelApe = document.getElementById('label-apellido');

        if (tipo === 'Juridico') {
            if (grupoRazon) grupoRazon.style.display = 'block';
            if (labelDoc) labelDoc.textContent = 'RUC (DGI) *';
            if (inputDoc) inputDoc.placeholder = 'J0310000001234';
            if (labelNom) labelNom.textContent = 'Nombre Contacto *';
            if (labelApe) labelApe.textContent = 'Apellido Contacto *';
        } else {
            if (grupoRazon) grupoRazon.style.display = 'none';
            if (labelDoc) labelDoc.textContent = 'Cédula de Identidad *';
            if (inputDoc) inputDoc.placeholder = '001-280590-0001A';
            if (labelNom) labelNom.textContent = 'Nombre(s) *';
            if (labelApe) labelApe.textContent = 'Apellido(s) *';
        }
    },

    toggleCamposTipoEditar(tipo) {
        const grupoRazon = document.getElementById('edit-grupo-razon-social');
        const labelDoc = document.getElementById('edit-label-documento');
        const inputDoc = document.getElementById('edit-c-documento');
        const labelNom = document.getElementById('edit-label-nombre');
        const labelApe = document.getElementById('edit-label-apellido');

        if (tipo === 'Juridico') {
            if (grupoRazon) grupoRazon.style.display = 'block';
            if (labelDoc) labelDoc.textContent = 'RUC (DGI) *';
            if (inputDoc) inputDoc.placeholder = 'J0310000001234';
            if (labelNom) labelNom.textContent = 'Nombre Contacto *';
            if (labelApe) labelApe.textContent = 'Apellido Contacto *';
        } else {
            if (grupoRazon) grupoRazon.style.display = 'none';
            if (labelDoc) labelDoc.textContent = 'Cédula de Identidad *';
            if (inputDoc) inputDoc.placeholder = '001-280590-0001A';
            if (labelNom) labelNom.textContent = 'Nombre(s) *';
            if (labelApe) labelApe.textContent = 'Apellido(s) *';
        }
    },

    async cambiarEstado(clienteId, nuevoEstado) {
        const accion = nuevoEstado ? 'activar' : 'desactivar';
        if (!confirm(`¿Confirma que desea ${accion} este cliente?`)) return;

        try {
            await API.patch(`/clientes/${clienteId}/estado`, { activo: nuevoEstado });
            UI.showToast(`Estado del cliente actualizado`, 'success');
            await this.cargarClientes();
        } catch (error) {
            // Error manejado por API wrapper
        }
    }
};

window.ClientesView = ClientesView;
