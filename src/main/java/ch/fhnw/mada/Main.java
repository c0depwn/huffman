package ch.fhnw.mada;

import ch.fhnw.mada.cli.Decode;
import ch.fhnw.mada.cli.Encode;
import ch.fhnw.mada.cli.EntryPoint;

public class Main {
    public static void main(String[] args) {
        EntryPoint entryPoint = new EntryPoint(System.out);
        entryPoint.register(new Decode(), new Encode());
        entryPoint.run(args.length > 0 ? args[0] : "", args);
    }
}
