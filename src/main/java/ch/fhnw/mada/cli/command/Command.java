package ch.fhnw.mada.cli.command;

import java.io.PrintStream;

public abstract class Command {
    private String icon;
    private String name;
    private String description;

    public Command() {}

    public abstract void run(PrintStream output);

    public abstract void configure();

    protected Command setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    protected Command setName(String name) {
        this.name = name;
        return this;
    }

    protected Command setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }
}
