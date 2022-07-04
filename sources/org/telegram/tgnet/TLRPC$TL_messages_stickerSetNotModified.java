package org.telegram.tgnet;

public class TLRPC$TL_messages_stickerSetNotModified extends TLRPC$TL_messages_stickerSet {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
