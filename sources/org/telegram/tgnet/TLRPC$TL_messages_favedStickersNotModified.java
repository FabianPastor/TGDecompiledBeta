package org.telegram.tgnet;

public class TLRPC$TL_messages_favedStickersNotModified extends TLRPC$messages_FavedStickers {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
