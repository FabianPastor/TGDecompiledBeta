package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$account_EmojiStatuses extends TLObject {
    public long hash;
    public ArrayList<TLRPC$EmojiStatus> statuses = new ArrayList<>();

    public static TLRPC$account_EmojiStatuses TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$account_EmojiStatuses tLRPC$TL_account_emojiStatuses;
        if (i != -NUM) {
            tLRPC$TL_account_emojiStatuses = i != -NUM ? null : new TLRPC$account_EmojiStatuses() { // from class: org.telegram.tgnet.TLRPC$TL_account_emojiStatusesNotModified
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        } else {
            tLRPC$TL_account_emojiStatuses = new TLRPC$TL_account_emojiStatuses();
        }
        if (tLRPC$TL_account_emojiStatuses != null || !z) {
            if (tLRPC$TL_account_emojiStatuses != null) {
                tLRPC$TL_account_emojiStatuses.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_account_emojiStatuses;
        }
        throw new RuntimeException(String.format("can't parse magic %x in account_EmojiStatuses", Integer.valueOf(i)));
    }
}
