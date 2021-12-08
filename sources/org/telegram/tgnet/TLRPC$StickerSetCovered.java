package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$StickerSetCovered extends TLObject {
    public TLRPC$Document cover;
    public ArrayList<TLRPC$Document> covers = new ArrayList<>();
    public TLRPC$StickerSet set;

    public static TLRPC$StickerSetCovered TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$StickerSetCovered tLRPC$StickerSetCovered;
        if (i != NUM) {
            tLRPC$StickerSetCovered = i != NUM ? null : new TLRPC$TL_stickerSetCovered();
        } else {
            tLRPC$StickerSetCovered = new TLRPC$TL_stickerSetMultiCovered();
        }
        if (tLRPC$StickerSetCovered != null || !z) {
            if (tLRPC$StickerSetCovered != null) {
                tLRPC$StickerSetCovered.readParams(abstractSerializedData, z);
            }
            return tLRPC$StickerSetCovered;
        }
        throw new RuntimeException(String.format("can't parse magic %x in StickerSetCovered", new Object[]{Integer.valueOf(i)}));
    }
}
