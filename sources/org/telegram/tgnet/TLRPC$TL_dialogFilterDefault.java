package org.telegram.tgnet;

public class TLRPC$TL_dialogFilterDefault extends TLRPC$DialogFilter {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
