package com.ultreon.data;

import com.ultreon.data.types.*;
import com.ultreon.data.util.DataTypeVisitor;

import java.io.*;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DataIo {

    private static final short VERSION = 3;
    private static final int HEADER = 0xff804269;
    private static final int BUFFER_SIZE = 4096;

    @SafeVarargs
    public static <T extends IType<?>> T read(File file, T... type) throws IOException {
        try (InputStream stream = new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE)) {
            return read(stream, type);
        }
    }

    @SafeVarargs
    public static <T extends IType<?>> T read(URL url, T... type) throws IOException {
        try (InputStream stream = new BufferedInputStream(url.openStream(), BUFFER_SIZE)) {
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
        if (stream instanceof DataInput) {
            return read((DataInput) stream, type);
        }
        return read((DataInput) new DataInputStream(stream), type);
    }

    /**
     * @throws IOException when an I/O error occurs.
     * @throws DataTypeException when the read data type is invalid.
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T extends IType<?>> T read(DataInput input, T... type) throws IOException {
        int magic = input.readInt();
        if (magic != HEADER) {
            throw new StreamCorruptedException(String.format("Invalid header got 0x%08X (expected 0xFF804269)", magic));
        }

        short readVersion = input.readShort();
        if (readVersion > VERSION) {
            throw new FutureVersionException(readVersion, VERSION);
        }

        Class<T> componentType = (Class<T>) type.getClass().getComponentType();
        int componentId = TypeRegistry.getId(componentType);
        int id = input.readUnsignedByte();

        if (componentId != id) {
            throw new DataTypeException("The read data id " + id + " is different from the expected id: " + componentId);
        }

        return (T) TypeRegistry.read(id, input);
    }

    @SafeVarargs
    public static <T extends IType<?>> T readCompressed(File file, T... type) throws IOException {
        try (InputStream stream = new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE)) {
            return readCompressed(stream, type);
        }
    }

    @SafeVarargs
    public static <T extends IType<?>> T readCompressed(URL url, T... type) throws IOException {
        try (InputStream stream = new BufferedInputStream(url.openStream())) {
            return readCompressed(stream, type);
        }
    }

    @SafeVarargs
    public static <T extends IType<?>> T readCompressed(InputStream stream, T... type) throws IOException {
        GZIPInputStream gzipStream = new GZIPInputStream(stream);
        return read(gzipStream, type);
    }

    public static void write(IType<?> type, File file) throws IOException {
        try (OutputStream stream = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE)) {
            write(type, stream);
        }
    }

    public static void write(IType<?> type, URL file) throws IOException {
        try (OutputStream stream = new BufferedOutputStream(file.openConnection().getOutputStream(), BUFFER_SIZE)) {
            write(type, stream);
        }
    }

    public static void write(IType<?> type, OutputStream stream) throws IOException {
        if (stream instanceof DataOutput) {
            write(type, (DataOutput) stream);
        }
        write(type, (DataOutput) new DataOutputStream(stream));
    }

    public static void write(IType<?> type, DataOutput output) throws IOException {
        output.writeInt(HEADER);
        output.writeShort(VERSION); // Version
        output.writeByte(type.id()); // Type
        type.write(output);
    }

    public static void writeCompressed(IType<?> type, URL file) throws IOException {
        try (OutputStream stream = new BufferedOutputStream(file.openConnection().getOutputStream(), BUFFER_SIZE)) {
            writeCompressed(type, stream);
        }
    }

    public static void writeCompressed(IType<?> type, File file) throws IOException {
        try (OutputStream stream = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE)) {
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
