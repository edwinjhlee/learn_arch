package agiledon.codekata.refactoring.duplication;

import java.io.PrintStream;

public class Template {


    public static String getCode(String template, int templateSplitBegin, int templateSplitEnd, String code){
        String templatePartOne = new String(template.substring(0, templateSplitBegin));
        String templatePartTwo =
                new String(template.substring(templateSplitEnd, template.length()));
        return new String(templatePartOne + code + templatePartTwo);
    }

    public void applyTemplate(String sourceTemplate, String reqId, PrintStream out) {

        try {
            String template = new String(sourceTemplate);

            // Substitute for %CODE%
            int templateSplitBegin = template.indexOf("%CODE%");
            String code = new String(reqId);
            template = getCode(template, templateSplitBegin, templateSplitBegin + 6, code);

            // Substitute for %ALTCODE%
            final int altcode_templateSplitBegin = template.indexOf("%ALTCODE%");
            final String tmp2 = getCode(template, altcode_templateSplitBegin, altcode_templateSplitBegin+9, code.substring(0, 5) + "-" + code.substring(5, 8);)

            out.print(tmp2);
        } catch (Exception e) {
            System.out.println("Error in substitute()");
        }
    }
}
