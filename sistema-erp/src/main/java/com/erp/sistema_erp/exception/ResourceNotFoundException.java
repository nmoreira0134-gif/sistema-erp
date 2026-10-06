/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: ResourceNotFoundException.java
 * PAQUETE: com.erp.sistema_erp.exception
 *
 * QUÉ HACE:
 * Excepción disparada cuando un recurso solicitado por ID o clave no existe o está eliminado.
 *
 * POR QUÉ EXISTE:
 * Estandariza la semántica de no encontrado en las capas de servicio y controladores.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es capturada por GlobalExceptionHandler para emitir una respuesta HTTP 404 Not Found limpia.
 */
package com.erp.sistema_erp.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
