package com.biblioteca.libreria.model;

import java.io.Serializable;
import java.util.Objects;

public class LibroHasCompraId implements Serializable {
    private Integer libro;
    private Integer compra;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LibroHasCompraId)) return false;
        LibroHasCompraId that = (LibroHasCompraId) o;
        return Objects.equals(libro, that.libro) && Objects.equals(compra, that.compra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(libro, compra);
    }
}
