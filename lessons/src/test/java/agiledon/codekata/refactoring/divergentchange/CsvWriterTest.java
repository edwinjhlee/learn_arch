package agiledon.codekata.refactoring.divergentchange;

import org.junit.Test;

public class CsvWriterTest {
    @Test
    public void should_write_content_with_csv_format() {
        //given
        String[] [] lines = new String[] [] {
                new String[] {},
                new String[] {"only one field"},
                new String[] {"two", "fields"},
                new String[] {"", "contents", "several words included"},
                new String[] {",", "embedded , commas, included", "trailing comma,"},
                new String[] {"\"", "embedded \" quotes", "multiple \"\"\" quotes\"\""},
                new String[] {"mixed commas, and \"quotes\"", "simple field"}
        };

        //when
        CsvWriter csvWriter = new CsvWriter();

        //then
        csvWriter.write(lines);
    }
}
