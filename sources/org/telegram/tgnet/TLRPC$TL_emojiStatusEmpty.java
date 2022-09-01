package org.telegram.tgnet;

public class TLRPC$TL_emojiStatusEmpty extends TLRPC$EmojiStatus {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
