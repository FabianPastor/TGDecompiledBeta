package org.telegram.tgnet;

public class TLRPC$TL_inputMediaEmpty extends TLRPC$InputMedia {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
