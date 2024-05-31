package com.es2.vadebicicleta.externo.commons.repository

class ElementNotFoundException : RuntimeException {
    constructor() : super()
    constructor(s: String?) : super(s)
}
