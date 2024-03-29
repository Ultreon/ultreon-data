package com.ultreon.data;

import com.ultreon.data.types.IType;

import java.io.DataInputStream;
import java.io.IOException;

@FunctionalInterface
public interface IReader<T extends IType<?>> {
    T read(DataInputStream stream) throws IOException;
}
