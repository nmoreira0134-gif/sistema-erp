/**
 * SISTEMA ERP - NICARAGUA
 * ARCHIVO: PaginaResponseDTO.java
 * PAQUETE: com.erp.sistema_erp.dto.common
 *
 * QUÉ HACE:
 * Encapsula de forma estandarizada los resultados de consultas paginadas del sistema ERP.
 *
 * POR QUÉ EXISTE:
 * Evita exponer directamente el objeto Page de Spring Data y proporciona una estructura JSON limpia
 * con metadatos útiles para la paginación en el frontend Vanilla (página actual, total, tamaño).
 *
 * CÓMO SE CONECTA CON EL RESTO:
 * Es utilizado por ClienteController, y servirá de estándar para futuros módulos (Facturas, Kardex, etc.).
 */
package com.erp.sistema_erp.dto.common;

import org.springframework.data.domain.Page;

import java.util.List;

public class PaginaResponseDTO<T> {

    private List<T> contenido;
    private int pagina;
    private int elementosPorPagina;
    private long totalElementos;
    private int totalPaginas;
    private boolean esPrimera;
    private boolean esUltima;

    public PaginaResponseDTO() {
    }

    public PaginaResponseDTO(List<T> contenido, int pagina, int elementosPorPagina, long totalElementos, int totalPaginas, boolean esPrimera, boolean esUltima) {
        this.contenido = contenido;
        this.pagina = pagina;
        this.elementosPorPagina = elementosPorPagina;
        this.totalElementos = totalElementos;
        this.totalPaginas = totalPaginas;
        this.esPrimera = esPrimera;
        this.esUltima = esUltima;
    }

    public static <T> PaginaResponseDTO<T> desde(Page<T> page) {
        return new PaginaResponseDTO<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    public List<T> getContenido() {
        return contenido;
    }

    public void setContenido(List<T> contenido) {
        this.contenido = contenido;
    }

    public int getPagina() {
        return pagina;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
    }

    public int getElementosPorPagina() {
        return elementosPorPagina;
    }

    public void setElementosPorPagina(int elementosPorPagina) {
        this.elementosPorPagina = elementosPorPagina;
    }

    public long getTotalElementos() {
        return totalElementos;
    }

    public void setTotalElementos(long totalElementos) {
        this.totalElementos = totalElementos;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }

    public void setTotalPaginas(int totalPaginas) {
        this.totalPaginas = totalPaginas;
    }

    public boolean isEsPrimera() {
        return esPrimera;
    }

    public void setEsPrimera(boolean esPrimera) {
        this.esPrimera = esPrimera;
    }

    public boolean isEsUltima() {
        return esUltima;
    }

    public void setEsUltima(boolean esUltima) {
        this.esUltima = esUltima;
    }
}
