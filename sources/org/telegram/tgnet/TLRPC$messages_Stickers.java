package org.telegram.tgnet;

public abstract class TLRPC$messages_Stickers extends TLObject {
    public static TLRPC$messages_Stickers TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_Stickers tLRPC$messages_Stickers;
        if (i != -NUM) {
            tLRPC$messages_Stickers = i != NUM ? null : new TLRPC$TL_messages_stickers();
        } else {
            tLRPC$messages_Stickers = new TLRPC$TL_messages_stickersNotModified();
        }
        if (tLRPC$messages_Stickers != null || !z) {
            if (tLRPC$messages_Stickers != null) {
                tLRPC$messages_Stickers.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_Stickers;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_Stickers", new Object[]{Integer.valueOf(i)}));
    }
}
