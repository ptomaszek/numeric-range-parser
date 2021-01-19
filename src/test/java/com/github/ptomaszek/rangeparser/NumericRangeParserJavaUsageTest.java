package com.github.ptomaszek.rangeparser;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class NumericRangeParserJavaUsageTest {

    @Test
    void showcase() {
        //given
        NumericRangeParser parser = NumericRangeParser.builder()
                .separatorSign(',')
                .rangeSign('-')
                .build();

        //when
        List<Integer> result = parser.parse("1-2, 1, 5, 8");

        //then
        Assertions.assertThat(result).containsExactly(1, 2, 5, 8);
    }
}
