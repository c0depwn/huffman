package ch.fhnw.mada.cli;

import ch.fhnw.mada.cli.command.Command;
import ch.fhnw.mada.cli.command.Option;
import ch.fhnw.mada.huffman.Decoder;

import java.io.PrintStream;
import java.nio.file.Path;

public class Decode extends Command {
    @Option(displayName = "TABLE_INPUT_FILE", flagName = "t")
    private String tableInput;
    @Option(displayName = "COMPRESSED_INPUT_FILE", flagName = "i")
    private String textInput;
    @Option(displayName = "OUTPUT_FILE", flagName = "o")
    private String outputFile;

    @Override
    public void run(PrintStream output) {
        var tableInputPath = Path.of(this.tableInput);
        var textInputPath = Path.of(this.textInput);
        var outputPath = Path.of(this.outputFile);

        if (!tableInputPath.toFile().exists()) {
            System.out.println(tableInput + " does not exist!");
            return;
        }
        if (!textInputPath.toFile().exists()) {
            System.out.println(textInput + " does not exist!");
            return;
        }

        System.out.println("... decoding your file");

        var decoder = new Decoder(
            tableInputPath,
            textInputPath,
            outputPath
        );
        decoder.decode();

        System.out.printf("...done!\ntext written to: %s\n", outputPath.toAbsolutePath());
    }

    @Override
    public void configure() {
        super.setIcon("🪄");
        super.setName("decode");
        super.setDescription("decode binary data using a huffman table");
    }
}
