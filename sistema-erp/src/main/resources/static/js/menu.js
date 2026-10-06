/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: menu.js
 * RUTA: src/main/resources/static/js/menu.js
 *
 * QUÉ HACE:
 * Renderiza dinámicamente el árbol de navegación del sidebar agrupando las pantallas autorizadas
 * por Módulo y gestionando la activación y carga de vistas en el contenedor central #app-content.
 *
 * POR QUÉ EXISTE:
 * Cumple con el requerimiento de interfaz modular, permitiendo que cada rol solo visualice y acceda
 * a las pantallas para las cuales tiene autorización en la base de datos.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Lee la lista de pantallas retornada por el login en Auth.getUsuario().menu y dispara la renderización
 * de UsuariosView, RolesView o vistas operativas.
 */

const Menu = {
    render(menuItems) {
        const menuContainer = document.getElementById('sidebar-menu-container');
        if (!menuContainer) return;

        menuContainer.innerHTML = '';

        if (!menuItems || menuItems.length === 0) {
            menuContainer.innerHTML = '<div style="padding: 1rem; color: #64748b; font-size: 0.8rem;">Sin pantallas asignadas</div>';
            return;
        }

        // Agrupar pantallas por Módulo
        const modulos = {};
        menuItems.forEach(item => {
            if (!modulos[item.modulo]) {
                modulos[item.modulo] = [];
            }
            modulos[item.modulo].push(item);
        });

        // Construir HTML del menú agrupado
        for (const [modulo, pantallas] of Object.entries(modulos)) {
            const moduloHeader = document.createElement('div');
            moduloHeader.className = 'menu-module-title';
            moduloHeader.textContent = modulo;
            menuContainer.appendChild(moduloHeader);

            pantallas.forEach(pantalla => {
                const link = document.createElement('a');
                link.className = 'menu-item';
                link.dataset.codigo = pantalla.codigo;
                link.dataset.nombre = pantalla.nombre;
                link.dataset.modulo = pantalla.modulo;
                link.innerHTML = `
                    <span style="width: 20px; text-align: center; font-weight: 700;">•</span>
                    <span class="menu-item-text">${pantalla.nombre}</span>
                `;

                link.addEventListener('click', (e) => {
                    e.preventDefault();
                    this.activarOpcion(link, pantalla);
                });

                menuContainer.appendChild(link);
            });
        }

        // Activar la primera opción disponible por defecto
        const primerEnlace = menuContainer.querySelector('.menu-item');
        if (primerEnlace) {
            primerEnlace.click();
        }
    },

    activarOpcion(elemento, pantalla) {
        document.querySelectorAll('.menu-item').forEach(el => el.classList.remove('active'));
        elemento.classList.add('active');

        // Actualizar título en topbar
        const breadcrumb = document.getElementById('topbar-breadcrumb');
        if (breadcrumb) {
            breadcrumb.textContent = `${pantalla.modulo} > ${pantalla.nombre}`;
        }

        // Cargar la vista correspondiente
        this.cargarVista(pantalla.codigo);
    },

    cargarVista(codigoPantalla) {
        switch (codigoPantalla) {
            case 'SEG_USUARIOS':
                UsuariosView.render();
                break;
            case 'SEG_ROLES':
                RolesView.render();
                break;
            case 'VEN_CLIENTES':
            case 'CLI_CLIENTES':
                ClientesView.render();
                break;
            default:
                UI.renderVistaEnConstruccion(codigoPantalla);
                break;
        }
    }
};

window.Menu = Menu;
