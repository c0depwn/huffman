package ch.fhnw.mada.cli;

import ch.fhnw.mada.cli.command.Command;
import ch.fhnw.mada.cli.command.Option;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntryPoint {
    private final HashMap<String, Command> commands;
    private final PrintStream output;

    public EntryPoint(PrintStream output) {
        this.commands = new HashMap<>();
        this.output = output;
    }

    public void register(Command... cmds) {
        for (Command cmd : cmds) {
            cmd.configure();
            this.commands.put(cmd.getName(), cmd);
        }
    }

    public void run(String name, String[] args) {
        if (name.equals("help") || name.isBlank()) {
            printHelp();
            return;
        }
        if (!this.commands.containsKey(name)) {
            output.println("⚠️ command " + "\"" + name + "\"" + " not found");
        }

        Command cmd = this.commands.get(name);
        parseArguments(cmd, args);

        try {
            cmd.run(this.output);
        } catch (Exception e) {
            output.println("\n⚠️ Something went wrong while running " + "\"" + cmd.getName() + "\"");
        }
    }

    /**
     * Parse command line arguments and set them on the command instance.
     * @param cmd Command
     * @param args String[]
     */
    private void parseArguments(Command cmd, String[] args) {
        List<String> argumentList = List.of(args).subList(1, args.length);
        Map<String, Option> fieldOptionMap = extractOptions(cmd);
        for (Map.Entry<String, Option> entry : fieldOptionMap.entrySet()) {
            String fieldName = entry.getKey();
            Option option = entry.getValue();

            int idx = argumentList.indexOf("-" + option.flagName());
            if (idx == -1 && option.required()) {
                output.println("⚠️ command \"" + cmd.getName() +  "\" requires option " + option.flagName() + " <" + option.displayName() + ">");
                return;
            }
            if (idx == -1) continue;

            if (argumentList.size() <= idx + 1) {
                output.println("⚠️ invalid arguments for command \"" + cmd.getName() +  "\"");
                return;
            }

            String value = argumentList.get(++idx);
            setOption(cmd, fieldName, value);
        }
    }

    /**
     * Extracts the options through the Option annotation and converts it to a Map which contains
     * the field name as the key and a reference to the Option as the value.
     * @param cmd Command
     * @return Map<String, Option>
     */
    private Map<String, Option> extractOptions(Command cmd) {
        // [f, f, f] -> { f: o, f: o, f: o }
        return Arrays.stream(cmd.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Option.class))
                .collect(Collectors.toMap(Field::getName, field -> field.getAnnotationsByType(Option.class)[0]));
    }

    /**
     * Sets the value for the configured option on the command instance using reflection.
     * @param cmd Command The command instance which the option value should be set on
     * @param fieldName String The name of the field to set on the instance
     * @param value String the value which will be set on the field
     */
    private void setOption(Command cmd, String fieldName, String value) {
        try {
            Field f = cmd.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(cmd, f.getType().cast(value));
        } catch (Exception e) {
            output.println("😨 something went wrong! The command \"" + cmd.getName() +  "\" might have a bug!");
        }
    }

    /**
     * Compute and prints the help by extracting all necessary information from the registered Command instances
     * using reflection.
     */
    private void printHelp() {
        StringBuilder cmdHelp = new StringBuilder("Huffman tool\nUsage:\n");

        for (Command cmd : this.commands.values()) {
            Map<String, Option> fieldOptionMap = extractOptions(cmd);
            String options = fieldOptionMap.values().stream()
                    .map(o -> String.format(" -%s <%s>", o.flagName(), o.displayName()))
                    .collect(Collectors.joining());
            cmdHelp.append(padding(String.format("%s %s%s", cmd.getIcon(), cmd.getName(), options), 72))
                    .append(cmd.getDescription())
                    .append("\n");
        }


        this.output.print(cmdHelp);
    }

    private String padding(String value, int targetLength) {
        if (value.length() == targetLength) {
            return value;
        }
        return String.format("%s%-" + (targetLength - value.length()) + "s", value, " ");
    }
}
