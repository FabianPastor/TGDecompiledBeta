package org.telegram.tgnet;

public class TLRPC$TL_webPageNotModified_layer110 extends TLRPC$TL_webPageNotModified {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
