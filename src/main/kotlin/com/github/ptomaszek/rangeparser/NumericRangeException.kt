package com.github.ptomaszek.rangeparser

class NumericRangeException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)
} 
