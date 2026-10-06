/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: ClienteController.java
 * PAQUETE: com.erp.sistema_erp.controller.ventas
 *
 * QUÉ HACE:
 * Controlador REST que expone los endpoints de gestión de Clientes (/api/clientes).
 *
 * POR QUÉ EXISTE:
 * Provee la interfaz HTTP para operaciones CRUD, consultas paginadas, búsquedas en tiempo real
 * y activación/desactivación lógica de clientes, asegurada mediante @PreAuthorize granular.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Invoca ClienteService y abastece la vista clientes.js del frontend Vanilla.
 */
package com.erp.sistema_erp.controller.ventas;

import com.erp.sistema_erp.dto.common.ApiResponse;
import com.erp.sistema_erp.dto.common.PaginaResponseDTO;
import com.erp.sistema_erp.dto.ventas.ClienteCreateDTO;
import com.erp.sistema_erp.dto.ventas.ClienteResponseDTO;
import com.erp.sistema_erp.dto.ventas.ClienteUpdateDTO;
import com.erp.sistema_erp.service.ventas.ClienteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('VEN_CLIENTES:ACCESO')")
    public ResponseEntity<ApiResponse<PaginaResponseDTO<ClienteResponseDTO>>> listar(
            @PageableDefault(size = 10, sort = "clienteId", direction = Sort.Direction.DESC) Pageable pageable) {
        PaginaResponseDTO<ClienteResponseDTO> pagina = clienteService.listarPaginado(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Clientes recuperados exitosamente", pagina));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('VEN_CLIENTES:ACCESO')")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> obtenerPorId(@PathVariable("id") Integer id) {
        ClienteResponseDTO cliente = clienteService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.exito(cliente));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('VEN_CLIENTES:ACCESO')")
    public ResponseEntity<ApiResponse<PaginaResponseDTO<ClienteResponseDTO>>> buscar(
            @RequestParam("q") String query,
            @PageableDefault(size = 10, sort = "clienteId", direction = Sort.Direction.DESC) Pageable pageable) {
        PaginaResponseDTO<ClienteResponseDTO> resultados = clienteService.buscar(query, pageable);
        return ResponseEntity.ok(ApiResponse.exito("Búsqueda completada", resultados));
    }

    @GetMapping("/buscar-rapido")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('VEN_CLIENTES:ACCESO')")
    public ResponseEntity<ApiResponse<List<ClienteResponseDTO>>> buscarRapido(@RequestParam("q") String query) {
        List<ClienteResponseDTO> resultados = clienteService.buscarRapido(query);
        return ResponseEntity.ok(ApiResponse.exito(resultados));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('VEN_CLIENTES:CREAR')")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> crear(@Valid @RequestBody ClienteCreateDTO dto) {
        ClienteResponseDTO creado = clienteService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Cliente registrado exitosamente", creado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('VEN_CLIENTES:EDITAR')")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> actualizar(
            @PathVariable("id") Integer id,
            @Valid @RequestBody ClienteUpdateDTO dto) {
        ClienteResponseDTO actualizado = clienteService.actualizar(id, dto);
        return ResponseEntity.ok(ApiResponse.exito("Cliente actualizado exitosamente", actualizado));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('VEN_CLIENTES:ELIMINAR')")
    public ResponseEntity<ApiResponse<String>> cambiarEstado(
            @PathVariable("id") Integer id,
            @RequestBody Map<String, Boolean> payload) {
        Boolean activo = payload.getOrDefault("activo", false);
        clienteService.cambiarEstado(id, activo);
        String mensaje = activo ? "Cliente activado exitosamente" : "Cliente desactivado exitosamente (Soft Delete)";
        return ResponseEntity.ok(ApiResponse.exito(mensaje, null));
    }
}
