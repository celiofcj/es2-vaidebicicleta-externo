package com.es2.vadebicicleta.externo.email.service;

public class WrongEmailAdressFormatException extends RuntimeException {
    public WrongEmailAdressFormatException(){
        super();
    }

    public WrongEmailAdressFormatException(String s){
        super(s);
    }
}
