# Huffman Code CLI

This is an oversimplified implementation of the Huffman algorithm to encode/decode text files.
This tool should not be used in any meaningful real-world way and is simply an implementation to gain better understanding on how the Huffman procedure works.

## Usage
To get an idea of what you can do with this tool try to run the `help` command.

## Encoding data
You can encode a text file using the `encode` command.
You need to supply it with an input file containing raw text using the `-i` option. 
Additionally, specify the path to where the compressed data and the huffman table will be written to using `-o` and `-t` respectively.
```
encode -i <INPUT_FILE> -o <DATA_OUTPUT_FILE> -t <TABLE_OUTPUT_FILE>
```

## Decoding data

Decoding data is done using the `decode` command. It expects you to supply the path where the output file `-o` will be written to, which will contain the original raw text.
Additionally, it needs the path to the file containing the huffman table (`-t`) and the compressed data (`-i`). 

```
decode -o <OUTPUT_FILE> -i <TEXT_INPUT_FILE> -t <TABLE_INPUT_FILE> 
```

## Assignment & Running with Maven (mvn)

Decode `output-mada.dat` using `dec_tab-mada.txt` and write result to `decompress.txt`.

```shell
mvn compile exec:java -Dexec.mainClass=ch.fhnw.mada.Main -Dexec.args="decode -o decompress.txt -i output-mada.dat -t dec_tab-mada.txt"
```

Encode `original.txt` and write table to `table.txt` and compressed data to `data.dat`.
```shell
mvn compile exec:java -Dexec.mainClass=ch.fhnw.mada.Main -Dexec.args="encode -i original.txt -t table.txt -o data.dat"
```

To ensure that the whole procedure is working we can decode `data.dat` using `table.txt` and expect to get the same text as we encoded.
```shell
mvn compile exec:java -Dexec.mainClass=ch.fhnw.mada.Main -Dexec.args="decode -o original2.txt -i data.dat -t table.txt"
```

Now `original.txt` and `original2.txt` should match. This can be checked manually or on a UNIX system you can use the `diff` command.
```shell
diff original.txt original2.txt
```

