package com.github.ptomaszek.numericrangeparser

import spock.lang.Specification

class NumericRangeParserTest extends Specification {

    def "default"() {
        given:
        def parser = NumericRangeParser.builder().build()

        expect:
        parser.parse(input) == expectedNumbers

        where:
        input    | expectedNumbers
        "1-2, 5" | [1, 2, 5]
    }

    def "pages parser with lenient requirements, i.e. some mistakes are allowed"() {
        given:
        def parser = new NumericRangeParser.Builder()
                .separatorSign(',' as char)
                .rangeSign('-' as char)
                .tolerateSpacesAdjoiningSeparator(true)
                .tolerateEmptyValuesBetweenSeparators(true)
                .build()

        expect:
        parser.parse(input) == expectedNumbers

        where:
        input                | expectedNumbers
        " "                  | []
        " , , "              | []
        "1,5"                | [1, 5]
        "1, 5"               | [1, 5]
        "1 ,5"               | [1, 5]
        " 1 ,  5"            | [1, 5]
        " 1 , , 15"          | [1, 15]
        "2-1"                | []
        "1-2,5"              | [1, 2, 5]
        "1-2 ,5"             | [1, 2, 5]
        "1-2, 5"             | [1, 2, 5]
        "1-2 ,  5"           | [1, 2, 5]
        "1-2 ,  5"           | [1, 2, 5]
        " 1-2 ,  5 "         | [1, 2, 5]
        " 1-2 , 4 ,6-8 "     | [1, 2, 4, 6, 7, 8]
        " 1-23 , 4 , 61-82 " | ((1..23) + (61..82))
    }

    def "invalid input for a pages parser"() {
        given:
        def parser = new NumericRangeParser.Builder()
                .separatorSign(',' as char)
                .rangeSign('-' as char)
                .tolerateSpacesAdjoiningSeparator(true)
                .tolerateEmptyValuesBetweenSeparators(false)
                .build()

        when:
        parser.parse(input)

        then:
        thrown NumericRangeException

        where:
        input << [
                " ,",
                "1-2-3",
                "0 1",
                "1-2 5",
                "0 1-2",
                " 0 1-2",
                " 0 1-2 ",
                "0, 1 - 2, 5",
                " 0 , 1- 2, 5",
                "1 -2, 5"
        ]
    }

    def "pages parser but with a space separator (event plenty of spaces)"() {
        given:
        def parser = new NumericRangeParser.Builder()
                .separatorSign(' ' as char)
                .rangeSign('-' as char)
                .tolerateSpacesAdjoiningSeparator(true)
                .tolerateEmptyValuesBetweenSeparators(true)
                .build()

        expect:
        parser.parse(input) == expectedNumbers

        where:
        input             | expectedNumbers
        " "               | []
        "  "              | []
        "1 5"             | [1, 5]
        " 1 5"            | [1, 5]
        "1 5 "            | [1, 5]
        "1  5"            | [1, 5]
        "2-1 "            | []
        "1-2 5"           | [1, 2, 5]
        "1-2  5"          | [1, 2, 5]
        " 1-23 4  61-82 " | ((1..23) + (61..82))
    }

    def "tolerate empty values/ranges"() {
        given:
        def parser = new NumericRangeParser.Builder()
                .separatorSign(',' as char)
                .rangeSign('-' as char)
                .tolerateSpacesAdjoiningSeparator(false)
                .tolerateEmptyValuesBetweenSeparators(true)
                .build()

        expect:
        parser.parse(input) == expectedNumbers

        where:
        input  | expectedNumbers
        ""     | []
        ",,"   | []
        "1,,5" | [1, 5]
    }

    def "invalid - don't tolerate empty values/ranges"() {
        given:
        def parser = new NumericRangeParser.Builder()
                .separatorSign(',' as char)
                .rangeSign('-' as char)
                .tolerateSpacesAdjoiningSeparator(true)
                .tolerateEmptyValuesBetweenSeparators(false)
                .build()


        when:
        parser.parse(input)

        then:
        thrown NumericRangeException

        where:
        input << [
                " ",
                ", , ",
                "1,,15"
        ]
    }

    def "invalid - don't tolerate spaces adjoining separators"() {
        given:
        def parser = new NumericRangeParser.Builder()
                .separatorSign(',' as char)
                .rangeSign('-' as char)
                .tolerateSpacesAdjoiningSeparator(false)
                .tolerateEmptyValuesBetweenSeparators(true)
                .build()


        when:
        parser.parse(input)

        then:
        thrown NumericRangeException

        where:
        input << [
                " ",
                ", , ",
                "1,, 15"
        ]
    }

    def "negatives"() {
        def parser = new NumericRangeParser.Builder()
                .separatorSign(',' as char)
                .rangeSign(':' as char)
                .tolerateSpacesAdjoiningSeparator(true)
                .build()

        expect:
        parser.parse(input) == expectedNumbers

        where:
        input            | expectedNumbers
        "1:2"            | [1, 2]
        "-1:2"           | (-1..2)
        " -12:-11 "      | [-12, -11]
        " -11:-12 "      | []
        " -1:2 ,5"       | (-1..2) + 5
        "-1:2, 5"        | (-1..2) + 5
        " -10, -1:2 ,5 " | [-10] + (-1..2) + 5
    }

    def "invalid - non-negatives fail when minus is a range sign"() {
        def parser = new NumericRangeParser.Builder()
                .rangeSign(',' as char)
                .rangeSign('-' as char)
                .build()

        when:
        parser.parse("-1:2")

        then:
        thrown NumericRangeException
    }

    def "distinct"() {
        def parser = new NumericRangeParser.Builder()
                .separatorSign(' ' as char)
                .rangeSign('-' as char)
                .distinct(distinct)
                .build()

        expect:
        parser.parse(input) == expectedNumbers

        where:
        input   | distinct | expectedNumbers
        "1-3 2" | true     | [1, 2, 3]
        "1-3 2" | false    | [1, 2, 3, 2]
    }

    def "sorted"() {
        def parser = new NumericRangeParser.Builder()
                .separatorSign(';' as char)
                .rangeSign(':' as char)
                .sorted(sorted)
                .build()

        expect:
        parser.parse(input) == expectedNumbers

        where:
        input       | sorted | expectedNumbers
        "-3:1; -10" | true   | [-10] + (-3..1)
        "-3:1; -10" | false  | (-3..1) + [-10]
    }

    def "invalid - big numbers"() {
        def parser = new NumericRangeParser.Builder()
                .separatorSign(';' as char)
                .rangeSign(':' as char)
                .build()

        when:
        parser.parse(input)

        then:
        thrown NumericRangeException

        where:
        input << [
                "${Integer.MAX_VALUE}0",
                "${Integer.MIN_VALUE}0"
        ]
    }

    def "invalid - too many numbers"() {
        def parser = new NumericRangeParser.Builder()
                .separatorSign(',' as char)
                .rangeSign(':' as char)
                .limit(10)
                .build()
        when:
        def a = parser.parse(input)
        print(a.size())

        then:
        thrown NumericRangeException

        where:
        input << [
                "-12:-2",
                "20:30",
                "-5:5",
                "5:0, -5:5",
        ]
    }

    def "numbers limit met"() {
        def parser = new NumericRangeParser.Builder()
                .separatorSign(',' as char)
                .rangeSign(':' as char)
                .limit(10)
                .build()
        when:
        def res = parser.parse(input)

        then:
        res.size() == expectedCount

        where:
        input                | expectedCount
        "-1:-15, 1:10"       | 10
        "-42, -5, -3:3, 667" | 10
    }

    def "invalid - unrecognized chars"() {
        def parser = new NumericRangeParser.Builder()
                .separatorSign(',' as char)
                .rangeSign('-' as char)
                .build()

        when:
        parser.parse(input)

        then:
        thrown NumericRangeException

        where:
        input << [
                "cv",
                "1-3, O"
        ]
    }
}
