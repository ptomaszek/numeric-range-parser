@file:Suppress("ComplexRedundantLet")

package com.github.ptomaszek.numericrangeparser

import mu.KotlinLogging


class NumericRangeParser(
    private val separatorSign: Char,
    private val rangeSign: Char,
    private val tolerateSpacesAdjoiningSeparator: Boolean,
    private val tolerateEmptyValuesBetweenSeparators: Boolean,
    private val distinct: Boolean,
    private val sorted: Boolean,
    private val limit: Int,
) {
    private val log = KotlinLogging.logger {}

    init {
        if (separatorSign == rangeSign) {
            throw NumericRangeException("Separator and range signs must be different")
        }
    }

    fun parse(input: String): List<Int> {
        if (input.isEmpty()) {
            return emptyList()
        }

        return input
            .let { splitToRanges(it) }
            .let { rangesToNumbers(it) }
            .let { if (distinct) it.distinct() else it }
            .let { if (sorted) it.sorted() else it }
    }

    private fun splitToRanges(input: String): List<Pair<Int, Int>> {
        return input
            .let { if (tolerateSpacesAdjoiningSeparator) it.replace("\\s+", " ") else it }
            .split(separatorSign)
            .let { if (tolerateSpacesAdjoiningSeparator) it.map { it.trim() }.toList() else it }
            .mapNotNull { toRange(it) }
    }

    private fun toRange(valueOrRange: String) = when {
        valueOrRange.isEmpty() -> {
            if (tolerateEmptyValuesBetweenSeparators) null else throw NumericRangeException("Must not contain empty values between separators")
        }
        valueOrRange.contains(rangeSign) -> {
            if (valueOrRange.count { it == rangeSign } > 1) {
                throw NumericRangeException("Range is broken $valueOrRange")
            }
            log.debug { "Found range: '$valueOrRange'" }
            val (start, end) = valueOrRange.split(rangeSign).map { value -> toInt(value) }
            Pair(start, end)
        }
        else -> {
            log.debug { "Found value: '$valueOrRange'" }
            Pair(toInt(valueOrRange), toInt(valueOrRange))
        }
    }

    private fun toInt(value: String): Int {
        return try {
            value.toInt()
        } catch (e: RuntimeException) {
            throw NumericRangeException(e)
        }
    }

    private fun rangesToNumbers(ranges: List<Pair<Int, Int>>): List<Int> {
        var total = 0
        return ranges
            .flatMap {
                val numbersInRange = (it.second + 1 - it.first).let { if (it == 0) 1 else it }
                if (numbersInRange < 0) {
                    log.debug { "Skipping invalid range: '$it'" }
                    emptyList()
                } else {
                    total += numbersInRange
                    if (total > limit) {
                        throw NumericRangeException("Too many numbers; increase limit")
                    }
                    IntRange(it.first, it.second).toList()
                }
            }
    }

    companion object {
        @JvmStatic
        fun builder(): Builder {
            return Builder()
        }
    }

    data class Builder(
        private var separatorSign: Char = ',',
        private var rangeSign: Char = '-',
        private var tolerateSpacesAdjoiningSeparator: Boolean = true,
        private var tolerateEmptyValuesBetweenSeparators: Boolean = false,
        private var distinct: Boolean = true,
        private var sorted: Boolean = false,
        private var limit: Int = 100_000,
    ) {

        fun separatorSign(char: Char) = apply {
            this.separatorSign = char
        }

        fun rangeSign(char: Char) = apply {
            this.rangeSign = char
        }

        fun limit(limit: Int) = apply {
            this.limit = limit
        }

        fun distinct(distinct: Boolean) = apply { this.distinct = distinct }
        fun tolerateSpacesAdjoiningSeparator(tolerateSpacesAdjoiningSeparator: Boolean) =
            apply { this.tolerateSpacesAdjoiningSeparator = tolerateSpacesAdjoiningSeparator }

        fun tolerateEmptyValuesBetweenSeparators(tolerateEmptyValuesBetweenSeparators: Boolean) =
            apply { this.tolerateEmptyValuesBetweenSeparators = tolerateEmptyValuesBetweenSeparators }

        /**
         * Natural sorting, i.e. ascending
         */
        fun sorted(sorted: Boolean) = apply { this.sorted = sorted }

        fun build() = NumericRangeParser(
            separatorSign = separatorSign,
            rangeSign = rangeSign,
            tolerateSpacesAdjoiningSeparator = tolerateSpacesAdjoiningSeparator,
            tolerateEmptyValuesBetweenSeparators = tolerateEmptyValuesBetweenSeparators,
            distinct = distinct,
            sorted = sorted,
            limit = limit
        )
    }
}
