package org.telegram.tgnet;

public class TLRPC$TL_pageBlockDivider extends TLRPC$PageBlock {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
