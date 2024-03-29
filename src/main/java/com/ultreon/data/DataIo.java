package com.ultreon.data;

import com.ultreon.data.types.*;
import com.ultreon.data.util.DataTypeVisitor;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.BitSet;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DataIo {
    private static final short VERSION = 3;
    private static final int HEADER = 0xff804269;

    @SafeVarargs
    public static <T extends IType<?>> T read(File file, T... type) throws IOException {
        try (FileInputStream stream = new FileInputStream(file)) {
            return read(stream, type);
        }
    }

    @SafeVarargs
    public static <T extends IType<?>> T read(URL url, T... type) throws IOException {
        try (InputStream stream = url.openStream()) {
            return read(stream, type);
        }
    }

    /**
     * @throws IOException when an I/O error occurs.
     * @throws DataTypeException when the read data type is invalid.
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T extends IType<?>> T read(InputStream stream, T... type) throws IOException {
        DataInputStream inputStream;
        if (stream instanceof DataInputStream) {
            inputStream = (DataInputStream) stream;
        } else {
            inputStream = new DataInputStream(stream);
        }

        int magic = inputStream.readInt();
        if (magic != HEADER) {
            throw new StreamCorruptedException(String.format("Invalid header got 0x%08X (expected 0xFF804269)", magic));
        }

        short readVersion = inputStream.readShort();
        if (readVersion > VERSION) {
            throw new FutureVersionException(readVersion, VERSION);
        }

        Class<T> componentType = (Class<T>) type.getClass().getComponentType();
        int componentId = TypeRegistry.getId(componentType);
        int id = inputStream.readUnsignedByte();

        if (componentId != id) {
            throw new DataTypeException("The read data id " + id + " is different from the expected id: " + componentId);
        }

        return (T) TypeRegistry.read(id, inputStream);
    }

    @SafeVarargs
    public static <T extends IType<?>> T readCompressed(File file, T... type) throws IOException {
        try (FileInputStream stream = new FileInputStream(file)) {
            return DataIo.readCompressed(stream, type);
        }
    }

    @SafeVarargs
    public static <T extends IType<?>> T readCompressed(URL url, T... type) throws IOException {
        try (InputStream stream = url.openStream()) {
            return readCompressed(stream, type);
        }
    }

    @SafeVarargs
    public static <T extends IType<?>> T readCompressed(InputStream stream, T... type) throws IOException {
        GZIPInputStream gzipStream = new GZIPInputStream(stream);
        return read(gzipStream, type);
    }

    public static void write(IType<?> type, File file) throws IOException {
        try (FileOutputStream stream = new FileOutputStream(file)) {
            write(type, stream);
        }
    }

    public static void write(IType<?> type, URL file) throws IOException {
        try (OutputStream stream = file.openConnection().getOutputStream()) {
            write(type, stream);
        }
    }

    public static void write(IType<?> type, OutputStream stream) throws IOException {
        DataOutputStream outputStream;
        if (stream instanceof DataOutputStream) {
            outputStream = (DataOutputStream) stream;
        } else {
            outputStream = new DataOutputStream(stream);
        }

        outputStream.writeInt(HEADER);
        outputStream.writeShort(VERSION); // Version
        outputStream.writeByte(type.id()); // Type
        type.write(outputStream);
        outputStream.flush();
    }

    public static void writeCompressed(IType<?> type, URL file) throws IOException {
        try (OutputStream stream = file.openConnection().getOutputStream()) {
            writeCompressed(type, stream);
        }
    }

    public static void writeCompressed(IType<?> type, File file) throws IOException {
        try (FileOutputStream stream = new FileOutputStream(file)) {
            writeCompressed(type, stream);
        }
    }

    public static void writeCompressed(IType<?> type, OutputStream stream) throws IOException {
        GZIPOutputStream gzipStream = new GZIPOutputStream(stream);
        write(type, gzipStream);
        gzipStream.finish();
        gzipStream.flush();
    }

    public static String toUso(IType<?> type) {
        return type.writeUso();
    }

    public static <T> T visit(DataTypeVisitor<T> visitor, IType<?> type) {
        return type.accept(visitor);
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <T extends IType<?>> T fromUso(String value, T... type) throws IOException {
        try (BufferedReader reader = new BufferedReader(new StringReader(value))) {
            IType<?> iType = readUso(reader.readLine());
            return (T) iType;
        }
    }

    private static IType<?> readUso(String value) throws IOException {
        return new UsoParser(value).parse();
    }
}
