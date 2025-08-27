package com.itb.inf2fm.projetoback.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Classe para respostas paginadas
 * Encapsula dados de paginação de forma padronizada
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Resposta paginada da API")
public class PagedResponse<T> {

    @Schema(description = "Lista de itens da página atual")
    private List<T> content;

    @Schema(description = "Número da página atual (zero-based)", example = "0")
    private int page;

    @Schema(description = "Tamanho da página", example = "20")
    private int size;

    @Schema(description = "Total de elementos", example = "150")
    private long totalElements;

    @Schema(description = "Total de páginas", example = "8")
    private int totalPages;

    @Schema(description = "Indica se é a primeira página", example = "true")
    private boolean first;

    @Schema(description = "Indica se é a última página", example = "false")
    private boolean last;

    @Schema(description = "Número de elementos na página atual", example = "20")
    private int numberOfElements;

    @Schema(description = "Indica se a página está vazia", example = "false")
    private boolean empty;

    public PagedResponse() {}

    public PagedResponse(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.numberOfElements = page.getNumberOfElements();
        this.empty = page.isEmpty();
    }

    // Factory method
    public static <T> PagedResponse<T> of(Page<T> page) {
        return new PagedResponse<>(page);
    }

    // Getters e Setters
    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
}
