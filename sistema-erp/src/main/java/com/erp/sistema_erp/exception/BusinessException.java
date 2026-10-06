/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: BusinessException.java
 * PAQUETE: com.erp.sistema_erp.exception
 *
 * QUÉ HACE:
 * Excepción personalizada para errores de reglas de negocio en el ERP (ej: usuario duplicado,
 * fondos insuficientes, correlativo agotado).
 *
 * POR QUÉ EXISTE:
 * Diferencia los errores lógicos del negocio de errores técnicos o de infraestructura.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es capturada por GlobalExceptionHandler para retornar respuestas HTTP 400 Bad Request estandarizadas.
 */
package com.erp.sistema_erp.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
