### Numeric range parser

#### Gradle

#### Maven

#### Examples

1. One-liner with defaults valid for parsing pages in format `1-2, 5`:

     ```java
     NumericRangeParser parser = NumericRangeParser.builder().build();
     ```

1. More explicit parser with lenient requirements for parsing input like " -5:14, &nbsp; , 56 &nbsp;" (notice redundant spaces
   and no value between separators):

     ```java
     NumericRangeParser parser = NumericRangeParser.builder()
            .separatorSign(',')
            .rangeSign(':')
            .tolerateSpacesAdjoiningSeparator(true)
            .tolerateEmptyValuesBetweenSeparators(true)
            .distinct(false)
            .sorted(true)
            .limit(1_000)
            .build();
     ```
2. See [tests](src/test/groovy/com/github/ptomaszek/rangeparser/NumericRangeParserTest.groovy) for more examples 