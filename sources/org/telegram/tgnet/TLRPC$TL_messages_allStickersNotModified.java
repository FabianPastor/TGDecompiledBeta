package org.telegram.tgnet;

public class TLRPC$TL_messages_allStickersNotModified extends TLRPC$messages_AllStickers {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
