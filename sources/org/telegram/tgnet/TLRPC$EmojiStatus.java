package org.telegram.tgnet;

public abstract class TLRPC$EmojiStatus extends TLObject {
    public static TLRPC$EmojiStatus TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$EmojiStatus tLRPC$EmojiStatus;
        if (i != -NUM) {
            tLRPC$EmojiStatus = i != NUM ? null : new TLRPC$TL_emojiStatusEmpty();
        } else {
            tLRPC$EmojiStatus = new TLRPC$TL_emojiStatus();
        }
        if (tLRPC$EmojiStatus != null || !z) {
            if (tLRPC$EmojiStatus != null) {
                tLRPC$EmojiStatus.readParams(abstractSerializedData, z);
            }
            return tLRPC$EmojiStatus;
        }
        throw new RuntimeException(String.format("can't parse magic %x in EmojiStatus", new Object[]{Integer.valueOf(i)}));
    }
}
