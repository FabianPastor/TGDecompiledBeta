package org.telegram.tgnet;

public class TLRPC$TL_textEmpty extends TLRPC$RichText {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
