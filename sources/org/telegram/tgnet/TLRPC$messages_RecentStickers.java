package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$messages_RecentStickers extends TLObject {
    public static TLRPC$messages_RecentStickers TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_RecentStickers tLRPC$TL_messages_recentStickers;
        if (i == -NUM) {
            tLRPC$TL_messages_recentStickers = new TLRPC$TL_messages_recentStickers();
        } else {
            tLRPC$TL_messages_recentStickers = i != NUM ? null : new TLRPC$messages_RecentStickers() { // from class: org.telegram.tgnet.TLRPC$TL_messages_recentStickersNotModified
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        }
        if (tLRPC$TL_messages_recentStickers != null || !z) {
            if (tLRPC$TL_messages_recentStickers != null) {
                tLRPC$TL_messages_recentStickers.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_messages_recentStickers;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_RecentStickers", Integer.valueOf(i)));
    }
}
