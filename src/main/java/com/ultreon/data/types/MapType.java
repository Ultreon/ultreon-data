package com.ultreon.data.types;

import com.ultreon.data.TypeRegistry;
import com.ultreon.data.Types;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class MapType implements IType<Map<String, IType<?>>> {
    private Map<String, IType<?>> obj;

    public MapType() {
        obj = new HashMap<>();
    }

    public MapType(Map<String, IType<?>> list) {
        setValue(list);
    }

    @Override
    public Map<String, IType<?>> getValue() {
        return obj;
    }

    @Override
    public void setValue(Map<String, IType<?>> obj) {
        this.obj = obj;
    }

    public Set<String> keys() {
        return obj.keySet();
    }

    public Set<Entry<String, IType<?>>> entries() {
        return obj.entrySet();
    }

    public Collection<IType<?>> values() {
        return obj.values();
    }

    @Override
    public int id() {
        return Types.MAP;
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(obj.size());
        for (Entry<String, IType<?>> e : obj.entrySet()) {
            stream.writeUTF(e.getKey());
            IType<?> value = e.getValue();
            stream.writeByte(value.id());
            value.write(stream);
        }
    }

    public static MapType read(DataInputStream stream) throws IOException {
        int len = stream.readInt();
        Map<String, IType<?>> map = new HashMap<>();
        for (int i = 0; i < len; i++) {
            String key = stream.readUTF();
            int id = stream.readUnsignedByte();
            map.put(key, TypeRegistry.read(id, stream));
        }

        return new MapType(map);
    }

    public boolean contains(String key, int type) {
        IType<?> data = obj.get(key);

        return data != null && data.id() == type;
    }

    @SafeVarargs
    public final <T extends IType<?>> boolean contains(String key, T... type) {
        IType<?> data = obj.get(key);

        return data != null && type.getClass().getComponentType().isAssignableFrom(data.getClass());
    }

    public void put(String key, IType<?> type) {
        obj.put(key, type);
    }

    public void putByte(String key, byte value) {
        put(key, new ByteType(value));
    }

    public void putByte(String key, int value) {
        put(key, new ByteType((byte) value));
    }

    public void putShort(String key, short value) {
        put(key, new ShortType(value));
    }

    public void putShort(String key, int value) {
        put(key, new ShortType((short) value));
    }

    public void putInt(String key, int value) {
        put(key, new IntType(value));
    }

    public void putLong(String key, long value) {
        put(key, new LongType(value));
    }

    public void putBigInt(String key, BigInteger value) {
        put(key, new BigIntType(value));
    }

    public void putFloat(String key, float value) {
        put(key, new FloatType(value));
    }

    public void putDouble(String key, double value) {
        put(key, new DoubleType(value));
    }

    public void putBigDec(String key, BigDecimal value) {
        put(key, new BigDecType(value));
    }

    public void putChar(String key, char value) {
        put(key, new CharType(value));
    }

    public void putBoolean(String key, boolean value) {
        put(key, new BooleanType(value));
    }

    public void putString(String key, String value) {
        put(key, new StringType(value));
    }

    public void putByteArray(String key, byte[] value) {
        put(key, new ByteArrayType(value));
    }

    public void putShortArray(String key, short[] value) {
        put(key, new ShortArrayType(value));
    }

    public void putIntArray(String key, int[] value) {
        put(key, new IntArrayType(value));
    }

    public void putLongArray(String key, long[] value) {
        put(key, new LongArrayType(value));
    }

    public void putFloatArray(String key, float[] value) {
        put(key, new FloatArrayType(value));
    }

    public void putDoubleArray(String key, double[] value) {
        put(key, new DoubleArrayType(value));
    }

    public void putCharArray(String key, char[] value) {
        put(key, new CharArrayType(value));
    }

    public void putBitSet(String key, byte[] value) {
        put(key, new BitSetType(value));
    }

    public void putBitSet(String key, BitSet value) {
        put(key, new BitSetType(value));
    }

    public void putUUID(String key, UUID value) {
        put(key, new UUIDType(value));
    }

    public byte getByte(String key) {
        return getByte(key, (byte) 0);
    }

    public byte getByte(String key, byte def) {
        IType<?> iType = get(key);
        if (iType instanceof ByteType) {
            return ((ByteType) iType).getValue();
        }
        return def;
    }

    public short getShort(String key) {
        return getShort(key, (byte) 0);
    }

    public short getShort(String key, short def) {
        IType<?> iType = get(key);
        if (iType instanceof ShortType) {
            return ((ShortType) iType).getValue();
        }
        return def;
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int def) {
        IType<?> iType = get(key);
        if (iType instanceof IntType) {
            return ((IntType) iType).getValue();
        }
        return def;
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public long getLong(String key, long def) {
        IType<?> iType = get(key);
        if (iType instanceof LongType) {
            return ((LongType) iType).getValue();
        }
        return def;
    }

    public BigInteger getBigInt(String key) {
        return getBigInt(key, BigInteger.ZERO);
    }

    public BigInteger getBigInt(String key, BigInteger def) {
        IType<?> iType = get(key);
        if (iType instanceof BigIntType) {
            return ((BigIntType) iType).getValue();
        }
        return def;
    }

    public float getFloat(String key) {
        return getFloat(key, 0);
    }

    public float getFloat(String key, float def) {
        IType<?> iType = get(key);
        if (iType instanceof FloatType) {
            return ((FloatType) iType).getValue();
        }
        return def;
    }

    public double getDouble(String key) {
        return getDouble(key, 0);
    }

    public double getDouble(String key, double def) {
        IType<?> iType = get(key);
        if (iType instanceof DoubleType) {
            return ((DoubleType) iType).getValue();
        }
        return def;
    }

    public BigDecimal getBigDec(String key) {
        return getBigDec(key, BigDecimal.ZERO);
    }

    public BigDecimal getBigDec(String key, BigDecimal def) {
        IType<?> iType = get(key);
        if (iType instanceof BigDecType) {
            return ((BigDecType) iType).getValue();
        }
        return def;
    }

    public char getChar(String key) {
        return getChar(key, (char) 0);
    }

    public char getChar(String key, char def) {
        IType<?> iType = get(key);
        if (iType instanceof CharType) {
            return ((CharType) iType).getValue();
        }
        return def;
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean def) {
        IType<?> iType = get(key);
        if (iType instanceof BooleanType) {
            return ((BooleanType) iType).getValue();
        }
        return def;
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String def) {
        IType<?> iType = get(key);
        if (iType instanceof StringType) {
            return ((StringType) iType).getValue();
        }
        return def;
    }

    public byte[] getByteArray(String key) {
        return getByteArray(key, null);
    }

    public byte[] getByteArray(String key, byte[] def) {
        IType<?> iType = get(key);
        if (iType instanceof ByteArrayType) {
            return ((ByteArrayType) iType).getValue();
        }
        return def;
    }

    public short[] getShortArray(String key) {
        return getShortArray(key, null);
    }

    public short[] getShortArray(String key, short[] def) {
        IType<?> iType = get(key);
        if (iType instanceof ShortArrayType) {
            return ((ShortArrayType) iType).getValue();
        }
        return def;
    }

    public int[] getIntArray(String key) {
        return getIntArray(key, null);
    }

    public int[] getIntArray(String key, int[] def) {
        IType<?> iType = get(key);
        if (iType instanceof IntArrayType) {
            return ((IntArrayType) iType).getValue();
        }
        return def;
    }

    public long[] getLongArray(String key) {
        return getLongArray(key, null);
    }

    public long[] getLongArray(String key, long[] def) {
        IType<?> iType = get(key);
        if (iType instanceof LongArrayType) {
            return ((LongArrayType) iType).getValue();
        }
        return def;
    }

    public float[] getFloatArray(String key) {
        return getFloatArray(key, null);
    }

    public float[] getFloatArray(String key, float[] def) {
        IType<?> iType = get(key);
        if (iType instanceof FloatArrayType) {
            return ((FloatArrayType) iType).getValue();
        }
        return def;
    }

    public double[] getDoubleArray(String key) {
        return getDoubleArray(key, null);
    }

    public double[] getDoubleArray(String key, double[] def) {
        IType<?> iType = get(key);
        if (iType instanceof DoubleArrayType) {
            return ((DoubleArrayType) iType).getValue();
        }
        return def;
    }

    public char[] getCharArray(String key) {
        return getCharArray(key, null);
    }

    public char[] getCharArray(String key, char[] def) {
        IType<?> iType = get(key);
        if (iType instanceof CharArrayType) {
            return ((CharArrayType) iType).getValue();
        }
        return def;
    }

    public BitSet getBitSet(String key) {
        return getBitSet(key, null);
    }

    public BitSet getBitSet(String key, BitSet def) {
        IType<?> iType = get(key);
        if (iType instanceof BitSetType) {
            return ((BitSetType) iType).getValue();
        }
        return def;
    }

    public MapType getMap(String key) {
        return getMap(key, null);
    }

    public MapType getMap(String key, MapType def) {
        IType<?> iType = get(key);
        if (iType instanceof MapType) {
            return (MapType) iType;
        }
        return def;
    }

    @SafeVarargs
    public final <T extends IType<?>> ListType<T> getList(String key, T... type) {
        return getList(key, new ListType<>(type));
    }

    public <T extends IType<?>> ListType<T> getList(String key, ListType<T> def) {
        IType<?> iType = get(key);
        if (iType instanceof ListType<?>) {
            ListType<?> obj = (ListType<?>) iType;
            if (obj.type() != def.type()) {
                return def;
            }
            return obj.cast(def.componentType);
        }
        return def;
    }

    public UUID getUUID(String key) {
        return getUUID(key, null);
    }

    public UUID getUUID(String key, UUID def) {
        IType<?> iType = get(key);
        if (iType instanceof UUIDType) {
            return ((UUIDType) iType).getValue();
        }
        return def;
    }

    public IType<?> get(String key) {
        return obj.get(key);
    }

    public boolean remove(String key) {
        return obj.remove(key, get(key));
    }

    public IType<?> pop(String key) {
        return obj.remove(key);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof MapType)) return false;
        MapType mapType = (MapType) other;
        return Objects.equals(obj, mapType.obj);
    }

    @Override
    public int hashCode() {
        return obj.hashCode();
    }

    @Override
    public MapType copy() {
        return new MapType(obj.entrySet().stream().collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().copy(), (a, b) -> b)));
    }

    @Override
    public String writeUso() {
        StringBuilder builder = new StringBuilder("{");
        for (Map.Entry<String, IType<?>> entry : obj.entrySet()) {
            builder.append("\"").append(entry.getKey().replace("\"", "\\\"")).append("\": ").append(entry.getValue().writeUso()).append(", ");
        }

        if (this.obj.size() > 1) {
            return builder.substring(0, builder.length() - 2) + "}";
        }

        return builder + "}";
    }

    public int size() {
        return obj.size();
    }

    public void clear() {
        obj.clear();
    }

    public boolean isEmpty() {
        return obj.isEmpty();
    }

    @Override
    public String toString() {
        return writeUso();
    }
}
