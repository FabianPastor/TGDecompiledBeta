package org.telegram.tgnet;

public abstract class TLRPC$PageListItem extends TLObject {
    public static TLRPC$PageListItem TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$PageListItem tLRPC$PageListItem;
        if (i != -NUM) {
            tLRPC$PageListItem = i != NUM ? null : new TLRPC$TL_pageListItemBlocks();
        } else {
            tLRPC$PageListItem = new TLRPC$TL_pageListItemText();
        }
        if (tLRPC$PageListItem != null || !z) {
            if (tLRPC$PageListItem != null) {
                tLRPC$PageListItem.readParams(abstractSerializedData, z);
            }
            return tLRPC$PageListItem;
        }
        throw new RuntimeException(String.format("can't parse magic %x in PageListItem", new Object[]{Integer.valueOf(i)}));
    }
}
