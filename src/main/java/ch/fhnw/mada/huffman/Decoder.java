package ch.fhnw.mada.huffman;

import ch.fhnw.mada.tree.BTree;
import ch.fhnw.mada.tree.Node;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * table + data -> raw text
 */
public class Decoder {
    private Path tableSource;
    private Path dataSource;
    private Path output;

    public Decoder(Path tableSource, Path dataSource, Path output) {
        this.tableSource = tableSource;
        this.dataSource = dataSource;
        this.output = output;
    }

    /**
     * Decode will perform the following pipeline:
     * 1. extract the huffman table from the tableSource and store it as a map where the value is the character
     *    and the key represents the bits used to encode the character.
     * 2. Each entry of the map will be inserted into a tree using the bits as the path for insertion in the tree
     * 3. Read the compressed data bit by bit and simultaneously step through the tree decoding each character into a string
     * 4. write the decoded data to the output path
     */
    public void decode() {
        // parse table from file
        var table = this.extractTable(this.tableSource);

        // fill ch.fhnw.mada.tree
        var tree = new BTree<>(new Node<String>());
        table.forEach(tree::insert);

        // extract result from source file using supplied table
        var result = extractData(this.dataSource, tree);

        this.writeDecompressedFile(this.output, result);
    }

    /**
     * Parses string of format ASCII_0:CODE_0-ASCII_1:CODE_1-... into a Map where the key is the ch.fhnw.mada.huffman code for
     * the character and the value is the ascii character.
     */
    private Map<int[], String> extractTable(Path source) {
        try (var reader = Files.newBufferedReader(source)) {
            return reader.lines()
                .map(l -> l.split("-"))
                .flatMap(Arrays::stream)
                .map(s -> s.split(":"))
                .collect(Collectors.toMap(s -> this.parseBinaryString(s[1]), s -> this.parseASCIIChar((s[0]))));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private int[] parseBinaryString(String binary) {
        return Arrays.stream(binary.split(""))
            .mapToInt(Integer::parseInt)
            .toArray();
    }

    private String parseASCIIChar(String charCode) {
        var code = Integer.parseInt(charCode);
        return String.valueOf((char) code);
    }

    private String extractData(Path source, BTree<String> tree) {
        var result = new StringBuilder();
        try (var inputStream = Files.newInputStream(source)) {
            // :( bad for memory but we don't care about it in this simple example implementation
            var content = inputStream.readAllBytes();

            // calculate padding
            var lastByte = content[content.length - 1];
            var lastBitIndex = (source.toFile().length() * 8) - 1;
            for (int i = 0; i < 8; i++) {
                // shift and logical AND to get bit at position
                var tempBit = lastByte >> i & 1;
                if (tempBit != 1) {
                    lastBitIndex -= 1;
                    continue;
                }
                lastBitIndex -= 1;
                break;
            }

            // decoding magic
            var index = 0;
            for (byte b : content) {
                for (int i = 7; 0 <= i; i--) {
                    // prevent reading of padding
                    if (index > lastBitIndex) break;

                    // shift and logical AND to get bit at position
                    var bit = (b >> i) & 1;
                    index += 1;

                    // traverse ch.fhnw.mada.tree on each bit
                    var node = tree.step(bit);
                    if (node != null && node.isLeaf()) {
                        result.append(node.getData());
                        tree.reset();
                    }
                }
            }

            return result.toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void writeDecompressedFile(Path target, String content) {
        try (var writer = Files.newBufferedWriter(target)) {
            writer.write(content);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
