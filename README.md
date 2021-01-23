## Numeric range parser

Easily read numeric ranges from text.

### Examples

1. One-liner with default settings appropriate for parsing pages for printing:
    ```java
     NumericRangeParser parser = NumericRangeParser.builder().build();
     
     parser.parse("1-2, 5");
     ```
1. More explicit parser creation with lenient input rules:

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
   
     parser.parse(" -5:14,  , 56  ");
     ```
2. See [tests](src/test/groovy/com/github/ptomaszek/rangeparser/NumericRangeParserTest.groovy) for more examples

### Installation

#### Gradle

```groovy 
implementation 'com.github.ptomaszek:numeric-range-parser:1.0.0'
```

#### Maven

```xml

<dependency>
    <groupId>com.github.ptomaszek</groupId>
    <artifactId>numeric-range-parser</artifactId>
    <version>1.0.0</version>
</dependency>
```
