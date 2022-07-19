package org.telegram.tgnet;

public class TLRPC$TL_updateReadFeaturedEmojiStickers extends TLRPC$Update {
    public static int constructor = -78886548;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
