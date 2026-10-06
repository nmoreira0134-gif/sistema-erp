/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: ClienteService.java
 * PAQUETE: com.erp.sistema_erp.service.ventas
 *
 * QUÉ HACE:
 * Servicio de lógica de negocio para la gestión integral de Clientes (CRUD, validaciones nicaragüenses,
 * generación secuencial de código y borrado lógico).
 *
 * POR QUÉ EXISTE:
 * Asegura el cumplimiento de las reglas fiscales y de control de crédito:
 * - Valida formatos de Cédula (Natural) y RUC (Jurídico).
 * - Exige Razón Social para personas jurídicas.
 * - Genera correlativos automáticos seguros CLI-{AÑO}-{SECUENCIA}.
 * - Gobierna el ciclo de vida del cliente mediante Soft Delete.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Utiliza ClienteRepository y expone métodos de negocio a ClienteController.
 */
package com.erp.sistema_erp.service.ventas;

import com.erp.sistema_erp.dto.common.PaginaResponseDTO;
import com.erp.sistema_erp.dto.ventas.ClienteCreateDTO;
import com.erp.sistema_erp.dto.ventas.ClienteResponseDTO;
import com.erp.sistema_erp.dto.ventas.ClienteUpdateDTO;
import com.erp.sistema_erp.exception.BusinessException;
import com.erp.sistema_erp.exception.ResourceNotFoundException;
import com.erp.sistema_erp.model.ventas.Cliente;
import com.erp.sistema_erp.repository.ventas.ClienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    // Patrón Cédula Natural Nicaragua: 001-280590-0001A (con o sin guiones)
    private static final Pattern PATRON_CEDULA = Pattern.compile("^[0-9]{3}-?[0-9]{6}-?[0-9]{4}[A-Za-z]$");

    // Patrón RUC Jurídico Nicaragua: J0310000001234 (Letra J seguida de 13 dígitos)
    private static final Pattern PATRON_RUC = Pattern.compile("^[Jj][0-9]{13}$");

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public PaginaResponseDTO<ClienteResponseDTO> listarPaginado(Pageable pageable) {
        Page<Cliente> pagina = clienteRepository.findAll(pageable);
        return PaginaResponseDTO.desde(pagina.map(this::convertirAResponseDTO));
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerPorId(Integer clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId));
        return convertirAResponseDTO(cliente);
    }

    @Transactional(readOnly = true)
    public PaginaResponseDTO<ClienteResponseDTO> buscar(String termino, Pageable pageable) {
        if (termino == null || termino.trim().isBlank()) {
            return listarPaginado(pageable);
        }
        Page<Cliente> pagina = clienteRepository.buscar(termino.trim(), pageable);
        return PaginaResponseDTO.desde(pagina.map(this::convertirAResponseDTO));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> buscarRapido(String termino) {
        if (termino == null || termino.trim().isBlank()) {
            return List.of();
        }
        return clienteRepository.buscarRapido(termino.trim()).stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    @Transactional
    public ClienteResponseDTO crear(ClienteCreateDTO dto) {
        validarReglasNicaragua(dto.getTipoCliente(), dto.getDocumentoIdentidad(), dto.getRazonSocial());

        // Validar unicidad del documento de identidad
        String docLimpio = dto.getDocumentoIdentidad().trim().toUpperCase();
        if (clienteRepository.contarPorDocumentoIdentidadTotal(docLimpio) > 0) {
            throw new BusinessException("El documento de identidad '" + docLimpio + "' ya está registrado en el sistema");
        }

        // Generar o validar código de cliente
        String codigoFinal;
        if (dto.getCodigoCliente() == null || dto.getCodigoCliente().trim().isBlank()) {
            codigoFinal = generarCodigoCliente();
        } else {
            codigoFinal = dto.getCodigoCliente().trim().toUpperCase();
            if (clienteRepository.contarPorCodigoClienteTotal(codigoFinal) > 0) {
                throw new BusinessException("El código de cliente '" + codigoFinal + "' ya existe");
            }
        }

        Cliente cliente = new Cliente();
        cliente.setCodigoCliente(codigoFinal);
        cliente.setNombre(dto.getNombre().trim());
        cliente.setApellido(dto.getApellido().trim());
        cliente.setRazonSocial(dto.getRazonSocial() != null ? dto.getRazonSocial().trim() : null);
        cliente.setTipoCliente(dto.getTipoCliente().trim());
        cliente.setDocumentoIdentidad(docLimpio);
        cliente.setEmail(dto.getEmail() != null ? dto.getEmail().trim().toLowerCase() : null);
        cliente.setTelefono(dto.getTelefono() != null ? dto.getTelefono().trim() : null);
        cliente.setDireccion(dto.getDireccion() != null ? dto.getDireccion().trim() : null);
        cliente.setCiudad(dto.getCiudad() != null ? dto.getCiudad().trim() : null);
        cliente.setPais(dto.getPais() != null && !dto.getPais().trim().isBlank() ? dto.getPais().trim() : "Nicaragua");

        // Condiciones de crédito
        BigDecimal limite = dto.getLimiteCredito() != null ? dto.getLimiteCredito() : BigDecimal.ZERO;
        if (limite.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("El límite de crédito no puede ser negativo");
        }
        cliente.setLimiteCredito(limite);

        Integer dias = dto.getDiasCredito() != null ? dto.getDiasCredito() : 0;
        if (dias < 0) {
            throw new BusinessException("Los días de crédito no pueden ser negativos");
        }
        // Si no tiene límite de crédito, es estrictamente de contado (días = 0)
        if (limite.compareTo(BigDecimal.ZERO) == 0) {
            dias = 0;
        }
        cliente.setDiasCredito(dias);
        cliente.setActivo(true);

        Cliente guardado = clienteRepository.save(cliente);
        return convertirAResponseDTO(guardado);
    }

    @Transactional
    public ClienteResponseDTO actualizar(Integer clienteId, ClienteUpdateDTO dto) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId));

        validarReglasNicaragua(dto.getTipoCliente(), dto.getDocumentoIdentidad(), dto.getRazonSocial());

        String docLimpio = dto.getDocumentoIdentidad().trim().toUpperCase();
        if (!cliente.getDocumentoIdentidad().equalsIgnoreCase(docLimpio) && clienteRepository.contarPorDocumentoIdentidadTotal(docLimpio) > 0) {
            throw new BusinessException("El documento de identidad '" + docLimpio + "' ya está registrado por otro cliente");
        }

        cliente.setNombre(dto.getNombre().trim());
        cliente.setApellido(dto.getApellido().trim());
        cliente.setRazonSocial(dto.getRazonSocial() != null ? dto.getRazonSocial().trim() : null);
        cliente.setTipoCliente(dto.getTipoCliente().trim());
        cliente.setDocumentoIdentidad(docLimpio);
        cliente.setEmail(dto.getEmail() != null ? dto.getEmail().trim().toLowerCase() : null);
        cliente.setTelefono(dto.getTelefono() != null ? dto.getTelefono().trim() : null);
        cliente.setDireccion(dto.getDireccion() != null ? dto.getDireccion().trim() : null);
        cliente.setCiudad(dto.getCiudad() != null ? dto.getCiudad().trim() : null);
        cliente.setPais(dto.getPais() != null && !dto.getPais().trim().isBlank() ? dto.getPais().trim() : "Nicaragua");

        BigDecimal limite = dto.getLimiteCredito() != null ? dto.getLimiteCredito() : BigDecimal.ZERO;
        if (limite.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("El límite de crédito no puede ser negativo");
        }
        cliente.setLimiteCredito(limite);

        Integer dias = dto.getDiasCredito() != null ? dto.getDiasCredito() : 0;
        if (dias < 0) {
            throw new BusinessException("Los días de crédito no pueden ser negativos");
        }
        if (limite.compareTo(BigDecimal.ZERO) == 0) {
            dias = 0;
        }
        cliente.setDiasCredito(dias);

        Cliente actualizado = clienteRepository.save(cliente);
        return convertirAResponseDTO(actualizado);
    }

    @Transactional
    public void cambiarEstado(Integer clienteId, boolean activo) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId));

        cliente.setActivo(activo);
        if (!activo) {
            cliente.setFechaEliminacion(LocalDateTime.now());
        } else {
            cliente.setFechaEliminacion(null);
        }
        clienteRepository.save(cliente);
    }

    /**
     * Valida reglas fiscales nicaragüenses según el tipo de cliente.
     */
    private void validarReglasNicaragua(String tipoCliente, String documentoIdentidad, String razonSocial) {
        if (tipoCliente == null || tipoCliente.isBlank()) {
            throw new BusinessException("El tipo de cliente es obligatorio ('Natural' o 'Juridico')");
        }

        String doc = documentoIdentidad != null ? documentoIdentidad.trim() : "";
        if (doc.isBlank()) {
            throw new BusinessException("El documento de identidad es obligatorio");
        }

        if ("Juridico".equalsIgnoreCase(tipoCliente)) {
            if (razonSocial == null || razonSocial.trim().isBlank()) {
                throw new BusinessException("La Razón Social es obligatoria para clientes de tipo Jurídico");
            }
            if (!PATRON_RUC.matcher(doc).matches()) {
                throw new BusinessException("El formato del RUC jurídico no es válido (Debe iniciar con 'J' seguido de 13 dígitos, ej: J0310000001234)");
            }
        } else if ("Natural".equalsIgnoreCase(tipoCliente)) {
            if (!PATRON_CEDULA.matcher(doc).matches()) {
                throw new BusinessException("El formato de la Cédula de identidad no es válido (ej: 001-280590-0001A)");
            }
        }
    }

    /**
     * Genera automáticamente el siguiente correlativo CLI-{AÑO}-{SECUENCIA}.
     */
    private synchronized String generarCodigoCliente() {
        int anioActual = LocalDate.now().getYear();
        String prefijo = "CLI-" + anioActual + "-";
        List<String> codigos = clienteRepository.findUltimosCodigosPorPrefijo(prefijo + "%");

        int siguienteNumero = 1;
        if (!codigos.isEmpty()) {
            String ultimoCodigo = codigos.get(0); // Mayor código existente
            try {
                String partes = ultimoCodigo.substring(prefijo.length());
                siguienteNumero = Integer.parseInt(partes) + 1;
            } catch (Exception ignored) {
                siguienteNumero = codigos.size() + 1;
            }
        }

        return String.format("%s%04d", prefijo, siguienteNumero);
    }

    private ClienteResponseDTO convertirAResponseDTO(Cliente c) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setClienteId(c.getClienteId());
        dto.setCodigoCliente(c.getCodigoCliente());
        dto.setNombre(c.getNombre());
        dto.setApellido(c.getApellido());
        dto.setNombreCompleto(c.getNombre() + " " + c.getApellido());
        dto.setRazonSocial(c.getRazonSocial());
        dto.setTipoCliente(c.getTipoCliente());
        dto.setDocumentoIdentidad(c.getDocumentoIdentidad());
        dto.setEmail(c.getEmail());
        dto.setTelefono(c.getTelefono());
        dto.setDireccion(c.getDireccion());
        dto.setCiudad(c.getCiudad());
        dto.setPais(c.getPais());
        dto.setLimiteCredito(c.getLimiteCredito());
        dto.setDiasCredito(c.getDiasCredito());
        dto.setEsCredito(c.getLimiteCredito() != null && c.getLimiteCredito().compareTo(BigDecimal.ZERO) > 0);
        dto.setActivo(c.getActivo());
        dto.setFechaCreacion(c.getFechaCreacion());
        dto.setCreadoPor(c.getCreadoPor());
        dto.setFechaModificacion(c.getFechaModificacion());
        dto.setModificadoPor(c.getModificadoPor());
        return dto;
    }
}
