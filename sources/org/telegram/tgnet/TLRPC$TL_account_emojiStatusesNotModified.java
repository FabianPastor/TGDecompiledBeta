package org.telegram.tgnet;

public class TLRPC$TL_account_emojiStatusesNotModified extends TLRPC$account_EmojiStatuses {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
