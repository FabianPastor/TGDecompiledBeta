package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$account_EmojiStatuses extends TLObject {
    public long hash;
    public ArrayList<TLRPC$EmojiStatus> statuses = new ArrayList<>();

    public static TLRPC$account_EmojiStatuses TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$account_EmojiStatuses tLRPC$account_EmojiStatuses;
        if (i != -NUM) {
            tLRPC$account_EmojiStatuses = i != -NUM ? null : new TLRPC$TL_account_emojiStatusesNotModified();
        } else {
            tLRPC$account_EmojiStatuses = new TLRPC$TL_account_emojiStatuses();
        }
        if (tLRPC$account_EmojiStatuses != null || !z) {
            if (tLRPC$account_EmojiStatuses != null) {
                tLRPC$account_EmojiStatuses.readParams(abstractSerializedData, z);
            }
            return tLRPC$account_EmojiStatuses;
        }
        throw new RuntimeException(String.format("can't parse magic %x in account_EmojiStatuses", new Object[]{Integer.valueOf(i)}));
    }
}
