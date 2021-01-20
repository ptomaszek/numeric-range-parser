package com.github.ptomaszek.numericrangeparser

class NumericRangeException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)
} 
