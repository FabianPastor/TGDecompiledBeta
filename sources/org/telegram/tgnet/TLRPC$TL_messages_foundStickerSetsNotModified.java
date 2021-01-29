package org.telegram.tgnet;

public class TLRPC$TL_messages_foundStickerSetsNotModified extends TLRPC$messages_FoundStickerSets {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
