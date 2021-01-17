package com.github.ptomaszek.rangeparser

import mu.KotlinLogging
import java.util.*
import kotlin.reflect.KProperty0


@Suppress("CanBeParameter")
class NumericRangeParser(
    private val separator: String,
    private val rangeSign: String,
    private val ignoreSpaces: Boolean,
    private val allowNegatives: Boolean,
    private val distinct: Boolean
) {
    private val log = KotlinLogging.logger {}

    private val preprocessors: List<Preprocessor>

    init {
        val preprocessorsTmp = mutableListOf<Preprocessor>()

        if (ignoreSpaces) {
            requireZeroSpaces(::separator)
            requireZeroSpaces(::rangeSign)
            preprocessorsTmp.add(SpacesRemovalPreprocessor())
        } else {
            requireNotBlank(::separator)
            requireNotBlank(::rangeSign)
        }

        if (allowNegatives) {
            requireNoHyphen(::separator)
            requireNoHyphen(::rangeSign)
        }

        preprocessors = Collections.unmodifiableList(preprocessorsTmp)
    }

    fun numbers(input: String): List<Int> {
        var processedInput = input
        preprocessors.forEach {
            processedInput = it.process(processedInput)
        }

        log.debug { "Preprocessed input: $processedInput" }

        val numbers = toRanges(processedInput)
            .also { log.debug { "Determined ranges: $it" } }
            .map { it.values() }
            .flatten()

        if (distinct) {
            return numbers.distinct()
        }
        return numbers
    }

    private fun toRanges(input: String): List<Range> {
        return input.split(separator)
            .map {
                log.debug { "Determining Range from $it" }
                if (it.contains(rangeSign)) {
                    val range = it.split(rangeSign)
                    if (range.size != 2) {
                        throw  IllegalArgumentException("Cannot determine range for: $it")
                    }
                    Range(range[0].toInt(), range[1].toInt())
                } else {
                    Range(it.toInt())
                }
            }
    }

    /**
     * Produce an example from based on the settings.
     * Can you use as an input hint.
     */
    fun example(): String {
        return "TODO"
    }

    private fun requireNotBlank(property: KProperty0<String>) {
        if (property.get().isBlank()) throw IllegalArgumentException("${property.name} must not be blank")
    }

    private fun requireZeroSpaces(property: KProperty0<String>) {
        if (property.get().contains(' ')) throw IllegalArgumentException("${property.name} must not contain spaces")
    }


    private fun requireNoHyphen(property: KProperty0<String>) {
        if (property.get().contains('-')) throw IllegalArgumentException("${property.name} must not contain hyphen")
    }

    data class Builder(
        var separator: String = ",",
        var rangeSign: String = "-",
        var ignoreSpaces: Boolean = true,
        var allowNegative: Boolean = false,
        var distinct: Boolean = true
    ) {

        fun separator(separator: String) = apply { this.separator = separator }
        fun rangeSign(rangeSign: String) = apply { this.rangeSign = rangeSign }

        /**
         * Space can be used as a separator
         */
        fun ignoreSpaces(ignoreSpaces: Boolean) = apply { this.ignoreSpaces = ignoreSpaces }
        fun allowNegative(allowNegative: Boolean) = apply { this.allowNegative = allowNegative }
        fun distinct(distinct: Boolean) = apply { this.distinct = distinct }

        fun build() = NumericRangeParser(
            separator,
            rangeSign,
            ignoreSpaces,
            allowNegative,
            distinct
        )
    }
}
