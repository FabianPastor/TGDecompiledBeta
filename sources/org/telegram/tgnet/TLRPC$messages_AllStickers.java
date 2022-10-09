package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$messages_AllStickers extends TLObject {
    public ArrayList<TLRPC$StickerSet> sets = new ArrayList<>();

    public TLRPC$messages_AllStickers() {
        new ArrayList();
        new ArrayList();
    }

    public static TLRPC$messages_AllStickers TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_AllStickers tLRPC$TL_messages_allStickers;
        if (i == -NUM) {
            tLRPC$TL_messages_allStickers = new TLRPC$TL_messages_allStickers();
        } else {
            tLRPC$TL_messages_allStickers = i != -NUM ? null : new TLRPC$messages_AllStickers() { // from class: org.telegram.tgnet.TLRPC$TL_messages_allStickersNotModified
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        }
        if (tLRPC$TL_messages_allStickers != null || !z) {
            if (tLRPC$TL_messages_allStickers != null) {
                tLRPC$TL_messages_allStickers.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_messages_allStickers;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_AllStickers", Integer.valueOf(i)));
    }
}
