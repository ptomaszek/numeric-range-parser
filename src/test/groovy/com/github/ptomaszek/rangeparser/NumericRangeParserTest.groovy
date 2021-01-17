package com.github.ptomaszek.rangeparser

import spock.lang.Specification

class NumericRangeParserTest extends Specification {

    def "default"(String stringRange, List<Integer> expectedNumbers) {
        def numericRangeParser = new NumericRangeParser.Builder()
                .build()

        expect:
        assert numericRangeParser.numbers(stringRange) == expectedNumbers

        where:
        stringRange | expectedNumbers
        "1,5"       | [1, 5]
        "1, 5"      | [1, 5]
        "1-2,5"     | [1, 2, 5]
        "1-2,4-5"   | [1, 2, 4, 5]
        "1-2,2-5"   | [1, 2, 3, 4, 5]
        "1-3,2"     | [1, 2, 3]
    }

    def "space separator"(String stringRange, List<Integer> expectedNumbers) {
        def numericRangeParser = new NumericRangeParser.Builder()
                .separator(" ")
                .ignoreSpaces(false)
                .build()

        expect:
        assert numericRangeParser.numbers(stringRange) == expectedNumbers

        where:
        stringRange | expectedNumbers
        "1 5"       | [1, 5]
        "1  5"      | [1, 5]
        "1-2 5"     | [1, 2, 5]
    }
}
