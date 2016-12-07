package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import java.nio.ByteBuffer;

public abstract class GroupEntry {
    public abstract ByteBuffer get();

    public abstract String getType();

    public abstract void parse(ByteBuffer byteBuffer);

    public int size() {
        return get().limit();
    }
}
