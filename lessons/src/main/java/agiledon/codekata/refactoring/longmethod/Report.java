package agiledon.codekata.refactoring.longmethod;

import java.io.*;
import java.util.*;

public class Report {
    public static void report(Writer out, List<Machine> machines, Robot robot) throws IOException {
        printHeader(out);
        printMachines(out, machines);
        printRobot(out, robot);
        printFooter(out);
    }

    private static void printFooter(Writer out) throws IOException {
        out.write("========\n");
    }

    private static void printRobot(Writer out, Robot robot) throws IOException {
        robot.print(out);
    }

    private static void printMachines(Writer out, List<Machine> machines) throws IOException {
        Iterator<Machine> line = machines.iterator();
        while (line.hasNext()) {
            Machine machine = (Machine) line.next();
            machine.print(out);
        }
        out.write("\n");
    }

    private static void printHeader(Writer out) throws IOException {
        out.write("FACTORY REPORT\n");
    }
}
