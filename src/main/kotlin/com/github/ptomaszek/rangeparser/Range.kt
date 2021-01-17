package com.github.ptomaszek.rangeparser

data class Range(val from: Int, val to: Int = from) {
    fun values(): List<Int> {
        return IntRange(from, to).toList()
    }
}
