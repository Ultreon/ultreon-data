package com.ultreon.data.types;

import com.ultreon.data.Types;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BooleanType implements IType<Boolean> {
    private boolean obj;

    public BooleanType(boolean obj) {
        this.obj = obj;
    }

    @Override
    public Boolean getValue() {
        return obj;
    }

    @Override
    public void setValue(Boolean obj) {
        if (obj == null) throw new IllegalArgumentException("Value can't be set to null");
        this.obj = obj;
    }

    @Override
    public int id() {
        return Types.BOOLEAN;
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeBoolean(obj);
    }

    public static BooleanType read(DataInputStream stream) throws IOException {
        return new BooleanType(stream.readBoolean());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof BooleanType)) return false;
        BooleanType that = (BooleanType) other;
        return obj == that.obj;
    }

    @Override
    public int hashCode() {
        return obj ? 1231 : 1237;
    }

    @Override
    public BooleanType copy() {
        return new BooleanType(obj);
    }

    @Override
    public String writeUso() {
        return obj ? "true" : "false";
    }

    @Override
    public String toString() {
        return writeUso();
    }
}
