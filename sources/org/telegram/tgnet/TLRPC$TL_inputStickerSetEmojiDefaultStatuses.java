package org.telegram.tgnet;

public class TLRPC$TL_inputStickerSetEmojiDefaultStatuses extends TLRPC$InputStickerSet {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
