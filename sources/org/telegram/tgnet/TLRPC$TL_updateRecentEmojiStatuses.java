package org.telegram.tgnet;

public class TLRPC$TL_updateRecentEmojiStatuses extends TLRPC$Update {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
