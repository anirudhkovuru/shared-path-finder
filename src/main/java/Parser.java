import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

class Parser {
    private static final String SCANNER_PATTERN = "[^\"\\s]+|\"(\\\\.|[^\\\\\"])*\"";

    static Command parse(String s) {
        Scanner scanner = new Scanner(s);
        String command = scanner.findInLine(SCANNER_PATTERN);
        ArrayList<String> args = new ArrayList<>();
        String arg;
        while ((arg = scanner.findInLine(SCANNER_PATTERN)) != null) {
            if (arg.startsWith("\"") && arg.endsWith("\"")) {
                args.add(arg.substring(1, arg.length() - 1));
            } else {
                args.add(arg);
            }
        }

        return new Command(command, Collections.unmodifiableList(args));
    }

    static class Command {
        private final String name;
        private final List<String> args;

        Command(String name, List<String> args) {
            this.name = name;
            this.args = args;
        }

        String getName() {
            return name;
        }

        List<String> getArgs() {
            return args;
        }

        @Override
        public String toString() {
            return String.format("%s (%s)", this.name, String.join(", ", this.args));
        }
    }
}
