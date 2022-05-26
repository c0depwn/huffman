package ch.fhnw.mada.cli;

import ch.fhnw.mada.cli.command.Command;
import ch.fhnw.mada.cli.command.Option;
import ch.fhnw.mada.huffman.Encoder;

import java.io.PrintStream;
import java.nio.file.Path;

public class Encode extends Command {
    @Option(displayName = "INPUT_FILE", flagName = "i")
    private String inputFile;
    @Option(displayName = "TABLE_OUTPUT_FILE", flagName = "t")
    private String tableOutput;
    @Option(displayName = "DATA_OUTPUT_FILE", flagName = "o")
    private String dataOutput;

    @Override
    public void run(PrintStream output) {
        var inputFilePath = Path.of(inputFile);
        var tableOutputPath = Path.of(tableOutput);
        var dataOutputPath = Path.of(dataOutput);

        if (!inputFilePath.toFile().exists()) {
            System.out.println(inputFile + " does not exist!");
            return;
        }

        System.out.println("... encoding your file");

        var encoder = new Encoder(
            inputFilePath,
            tableOutputPath,
            dataOutputPath
        );
        encoder.encode();

        System.out.printf(
            "...done!\n table stored in: %s\ncompressed data stored in: %s\n",
            tableOutputPath.toAbsolutePath(),
            dataOutputPath.toAbsolutePath()
        );
    }

    @Override
    public void configure() {
        super.setIcon("\uD83D\uDCE6"); // somehow the package emoji does not print properly when using the emoji instead of unicode
        super.setName("encode");
        super.setDescription("encode text using ch.fhnw.mada.huffman algorithm, outputs a table used by the algorithm and a compressed binary data file");
    }
}
