package com.ultreon.data.types;

import com.ultreon.data.Types;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class FloatType implements IType<Float> {
    private float obj;

    public FloatType(float obj) {
        this.obj = obj;
    }

    @Override
    public Float getValue() {
        return obj;
    }

    @Override
    public void setValue(Float obj) {
        if (obj == null) throw new IllegalArgumentException("Value can't be set to null");
        this.obj = obj;
    }

    @Override
    public int id() {
        return Types.FLOAT;
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeFloat(obj);
    }

    public static FloatType read(DataInputStream stream) throws IOException {
        return new FloatType(stream.readFloat());
    }
}
