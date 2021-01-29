package org.telegram.tgnet;

public abstract class TLRPC$messages_RecentStickers extends TLObject {
    public static TLRPC$messages_RecentStickers TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_RecentStickers tLRPC$messages_RecentStickers;
        if (i != NUM) {
            tLRPC$messages_RecentStickers = i != NUM ? null : new TLRPC$TL_messages_recentStickers();
        } else {
            tLRPC$messages_RecentStickers = new TLRPC$TL_messages_recentStickersNotModified();
        }
        if (tLRPC$messages_RecentStickers != null || !z) {
            if (tLRPC$messages_RecentStickers != null) {
                tLRPC$messages_RecentStickers.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_RecentStickers;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_RecentStickers", new Object[]{Integer.valueOf(i)}));
    }
}
