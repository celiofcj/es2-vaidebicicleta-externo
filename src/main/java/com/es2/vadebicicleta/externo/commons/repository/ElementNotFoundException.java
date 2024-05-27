package com.es2.vadebicicleta.externo.commons.repository;

public class ElementNotFoundException extends RuntimeException {
    public ElementNotFoundException() {
        super();
    }

    public ElementNotFoundException(String s) {
        super(s);
    }
}
