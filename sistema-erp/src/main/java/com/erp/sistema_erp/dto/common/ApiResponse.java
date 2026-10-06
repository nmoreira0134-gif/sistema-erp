/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: ApiResponse.java
 * PAQUETE: com.erp.sistema_erp.dto.common
 *
 * QUÉ HACE:
 * Envoltorio estándar genérico para todas las respuestas JSON emitidas por la API REST del ERP.
 *
 * POR QUÉ EXISTE:
 * Proporciona un contrato uniforme de respuesta que contiene éxito (booleano), mensaje amigable,
 * datos de carga útil (payload genérico T) y estampa de tiempo ISO.
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es retornado por todos los @RestController y por el GlobalExceptionHandler.
 */
package com.erp.sistema_erp.dto.common;

import java.time.LocalDateTime;

public class ApiResponse<T> {

    private boolean exito;
    private String mensaje;
    private T datos;
    private LocalDateTime timestamp;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(boolean exito, String mensaje, T datos) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.datos = datos;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> exito(String mensaje, T datos) {
        return new ApiResponse<>(true, mensaje, datos);
    }

    public static <T> ApiResponse<T> exito(T datos) {
        return new ApiResponse<>(true, "Operación exitosa", datos);
    }

    public static <T> ApiResponse<T> error(String mensaje) {
        return new ApiResponse<>(false, mensaje, null);
    }

    public static <T> ApiResponse<T> error(String mensaje, T detalles) {
        return new ApiResponse<>(false, mensaje, detalles);
    }

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public T getDatos() {
        return datos;
    }

    public void setDatos(T datos) {
        this.datos = datos;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
