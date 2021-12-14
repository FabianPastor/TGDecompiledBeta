package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$messages_StickerSet extends TLObject {
    public ArrayList<TLRPC$Document> documents = new ArrayList<>();
    public ArrayList<TLRPC$TL_stickerPack> packs = new ArrayList<>();
    public TLRPC$StickerSet set;

    public static TLRPC$TL_messages_stickerSet TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet;
        if (i != -NUM) {
            tLRPC$TL_messages_stickerSet = i != -NUM ? null : new TLRPC$TL_messages_stickerSetNotModified();
        } else {
            tLRPC$TL_messages_stickerSet = new TLRPC$TL_messages_stickerSet();
        }
        if (tLRPC$TL_messages_stickerSet != null || !z) {
            if (tLRPC$TL_messages_stickerSet != null) {
                tLRPC$TL_messages_stickerSet.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_messages_stickerSet;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_StickerSet", new Object[]{Integer.valueOf(i)}));
    }
}
