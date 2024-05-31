package com.es2.vadebicicleta.externo.email.service

class WrongEmailAdressFormatException : RuntimeException {
    constructor() : super()
    constructor(s: String?) : super(s)
}
