package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$EmojiStatus extends TLObject {
    public static TLRPC$EmojiStatus TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$EmojiStatus tLRPC$TL_emojiStatus;
        if (i == -NUM) {
            tLRPC$TL_emojiStatus = new TLRPC$TL_emojiStatus();
        } else if (i != -97474361) {
            tLRPC$TL_emojiStatus = i != NUM ? null : new TLRPC$TL_emojiStatusEmpty();
        } else {
            tLRPC$TL_emojiStatus = new TLRPC$TL_emojiStatusUntil();
        }
        if (tLRPC$TL_emojiStatus != null || !z) {
            if (tLRPC$TL_emojiStatus != null) {
                tLRPC$TL_emojiStatus.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_emojiStatus;
        }
        throw new RuntimeException(String.format("can't parse magic %x in EmojiStatus", Integer.valueOf(i)));
    }
}
