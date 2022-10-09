package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$PageListOrderedItem extends TLObject {
    public static TLRPC$PageListOrderedItem TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$PageListOrderedItem tLRPC$TL_pageListOrderedItemBlocks;
        if (i != -NUM) {
            tLRPC$TL_pageListOrderedItemBlocks = i != NUM ? null : new TLRPC$TL_pageListOrderedItemText();
        } else {
            tLRPC$TL_pageListOrderedItemBlocks = new TLRPC$TL_pageListOrderedItemBlocks();
        }
        if (tLRPC$TL_pageListOrderedItemBlocks != null || !z) {
            if (tLRPC$TL_pageListOrderedItemBlocks != null) {
                tLRPC$TL_pageListOrderedItemBlocks.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_pageListOrderedItemBlocks;
        }
        throw new RuntimeException(String.format("can't parse magic %x in PageListOrderedItem", Integer.valueOf(i)));
    }
}
