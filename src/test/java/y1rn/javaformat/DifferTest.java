package y1rn.javaformat;

import static java.util.Comparator.comparing;

import com.github.difflib.patch.PatchFailedException;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.googlejavaformat.Newlines;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.google.googlejavaformat.java.ImportOrderer;
import com.google.googlejavaformat.java.JavaFormatterOptions;
import com.google.googlejavaformat.java.RemoveUnusedImports;
import com.google.googlejavaformat.java.JavaFormatterOptions.Style;
import com.google.googlejavaformat.java.Replacement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class DifferTest {

  @Test
  void diffFormat() throws IOException, PatchFailedException, FormatterException {

    String input = Files.readString(Path.of("/root/temp/vscode-google-java-format/java-format-service/src/main/java/y1rn/javaformat/Request.java"));

    JavaFormatterOptions options = JavaFormatterOptions.builder().style(Style.GOOGLE).build();
    Formatter formater = new Formatter(options);

    RangeSet<Integer> range =
        Formatter.lineRangesToCharRanges(input, ImmutableRangeSet.of(Range.closedOpen(9, 10)));
    List<Replacement> replacements = formater.getFormatReplacements(input, range.asRanges());

    replacements = new ArrayList<>(replacements);
    replacements.sort(comparing((Replacement r) -> r.getReplaceRange().lowerEndpoint()).reversed());
    for (Replacement replacement : replacements) {
      System.out.printf(
          "%s, %s\n",
          replacement.getReplaceRange().lowerEndpoint(),
          replacement.getReplaceRange().upperEndpoint());
      System.out.println(replacement.getReplacementString());
    }
    // assert replacements.size() == 0;

    String output = formater.formatSource(input);
    // String output = nformater.formatSource(input,ImmutableList.of(Range.closedOpen(8, 10)));
    output = RemoveUnusedImports.removeUnusedImports(output);
    output = ImportOrderer.reorderImports(output, Style.GOOGLE);
    String sep = Newlines.guessLineSeparator(input);
    List<TextEdit> respResult = Differ.getTextEdit(input, output, sep);
    assert respResult.size() == 0;
  }
}

