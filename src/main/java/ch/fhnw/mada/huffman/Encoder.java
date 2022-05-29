package ch.fhnw.mada.huffman;

import ch.fhnw.mada.tree.Node;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * raw text -> table + data
 */
public class Encoder {
    private Path dataSource;
    private Path tableOutput;
    private Path compressedOutput;

    private long initialSize = 0;
    private long compressedSize = 0;

    public Encoder(Path dataSource, Path tableOutput, Path compressedOutput) {
        this.dataSource = dataSource;
        this.tableOutput = tableOutput;
        this.compressedOutput = compressedOutput;
    }

    /**
     * Encode will perform the following pipeline:
     * 1. read each character from the dataSource
     * 2. create a map containing the occurrence of each character as the value and the character as the key
     * 3. use a priority queue to create the huffman tree
     * 4. recursively traverse the tree to create a map which contains the character as the key and the huffman code as the value
     * 5a write the huffman table to tableOutput
     * 5b read the dataSource convert each character and write it to the compressedOutput path
     */
    public void encode() {
        var charCount = this.read(this.dataSource);

        // fill queue and allow it to compare the occurrence of each character
        var queue = new PriorityQueue<Node<HuffmanData>>(Comparator.comparingLong(n -> n.getData().count));
        charCount.forEach((c, count) -> {
            var node = new Node<>(new HuffmanData(count, c));
            queue.add(node);
        });

        // create tree from the queue (bottom up)
        Node<HuffmanData> root = null;
        while (queue.size() > 1) {
            var l = queue.poll();
            var r = queue.poll();

            var parent = new Node<>(new HuffmanData());
            parent.addLeft(l);
            parent.addRight(r);

            parent.getData().count = l.getData().count;
            if (r != null) {
                parent.getData().count += r.getData().count;
            }

            root = parent;
            queue.add(parent);
        }

        // fill a map in the form of (Char -> Binary String)
        var charCodeMap = new HashMap<Character, String>();
        this.getCodes(root, "", charCodeMap);

        this.writeTable(charCodeMap, this.tableOutput);
        this.writeData(charCodeMap, this.compressedOutput, this.dataSource);
    }

    /**
     * Traverse supplied tree in DFS, every time a leaf is hit it is added to the map resulting in the map
     * having the character as key and the encoded binary string value for said character as the value.
     * @param node the root node of the tree
     * @param code initially empty string will contain the huffman code (left = 0, right = 1) as the tree is traversed
     * @param charCodeMap the map to populate
     */
    private void getCodes(Node<HuffmanData> node, String code, HashMap<Character, String> charCodeMap) {
        if (node.getLeft() != null) {
            getCodes(node.getLeft(), code + "0", charCodeMap);
        }
        if (node.getRight() != null) {
            getCodes(node.getRight(), code + "1", charCodeMap);
        }
        if (node.getData() != null && node.getData().c != 0) {
            charCodeMap.put(node.getData().c, code);
        }
    }

    private Map<Character, Long> read(Path source) {
        try (var reader = Files.newBufferedReader(source)) {
            this.initialSize = Files.size(source);

            // read each byte as char
            var characters = new ArrayList<Character>();
            int b;
            while ((b = reader.read()) != -1 ) {
                characters.add((char) b);
            }

            // use groupingBy to conveniently count occurrences of chars
            return characters.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void writeTable(Map<Character, String> charCodeMap, Path targetTable) {
        try (var writer = Files.newBufferedWriter(targetTable)) {
            var lastIndex = charCodeMap.entrySet().size() - 1;
            var idx = 0;
            for (var entry : charCodeMap.entrySet()) {
                var sb = new StringBuilder();
                sb.append((int)entry.getKey());
                sb.append(":");
                sb.append(entry.getValue());
                if (idx < lastIndex) {
                    sb.append("-");
                }
                var toWrite = sb.toString();
                writer.write(toWrite);
                idx += 1;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void writeData(Map<Character, String> charCodeMap, Path targetOutput, Path input) {
        try (
            var reader = Files.newInputStream(input);
            var writer = Files.newOutputStream(targetOutput)
        ) {
            var bitString = new StringBuilder();

            // encode
            int b;
            while ((b = reader.read()) != -1 ) {
                if (!charCodeMap.containsKey((char)b)) {
                    throw new IOException("char '" + (char)b + "' not found in char code table");
                }
                bitString.append(charCodeMap.get((char) b));
            }

            // padding
            var padLength = 8 - (bitString.length() % 8);
            for (int i = 0; i < padLength; i++) {
                if (i == 0) {
                    bitString.append("1");
                    continue;
                }
                bitString.append("0");
            }

            // write to output
            var bits = bitString.toString();
            this.compressedSize = bits.length() / 8;
            for (int i = 0; i < bits.length(); i += 8) {
                var temp = Integer.parseInt(bits.substring(i, i + 8) ,2);
                writer.write((byte) temp);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public long getInitialSize() {
        return initialSize;
    }

    public long getCompressedSize() {
        return compressedSize;
    }
}

class HuffmanData {
    long count;
    char c;

    public HuffmanData() {
        this.count = 0;
    }

    public HuffmanData(long count, char c) {
        this.count = count;
        this.c = c;
    }

    @Override
    public String toString() {
        return c + " #" + count;
    }
}