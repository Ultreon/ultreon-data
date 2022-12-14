package com.ultreon.data.types;

import com.ultreon.data.Types;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class CharType implements IType<Character> {
    private char obj;

    public CharType(char obj) {
        this.obj = obj;
    }

    @Override
    public Character getValue() {
        return obj;
    }

    @Override
    public void setValue(Character obj) {
        if (obj == null) throw new IllegalArgumentException("Value can't be set to null");
        this.obj = obj;
    }

    @Override
    public int id() {
        return Types.CHAR;
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeChar(obj);
    }

    public static CharType read(DataInputStream stream) throws IOException {
        return new CharType(stream.readChar());
    }
}
