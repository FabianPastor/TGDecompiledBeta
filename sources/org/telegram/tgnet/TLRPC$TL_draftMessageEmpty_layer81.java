package org.telegram.tgnet;

public class TLRPC$TL_draftMessageEmpty_layer81 extends TLRPC$TL_draftMessageEmpty {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
