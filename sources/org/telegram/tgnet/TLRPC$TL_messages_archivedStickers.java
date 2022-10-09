package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public class TLRPC$TL_messages_archivedStickers extends TLObject {
    public static int constructor = NUM;
    public int count;
    public ArrayList<TLRPC$StickerSetCovered> sets = new ArrayList<>();

    public static TLRPC$TL_messages_archivedStickers TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_messages_archivedStickers", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_messages_archivedStickers tLRPC$TL_messages_archivedStickers = new TLRPC$TL_messages_archivedStickers();
        tLRPC$TL_messages_archivedStickers.readParams(abstractSerializedData, z);
        return tLRPC$TL_messages_archivedStickers;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.count = abstractSerializedData.readInt32(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        if (readInt32 != NUM) {
            if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
            }
            return;
        }
        int readInt322 = abstractSerializedData.readInt32(z);
        for (int i = 0; i < readInt322; i++) {
            TLRPC$StickerSetCovered TLdeserialize = TLRPC$StickerSetCovered.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if (TLdeserialize == null) {
                return;
            }
            this.sets.add(TLdeserialize);
        }
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.count);
        abstractSerializedData.writeInt32(NUM);
        int size = this.sets.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.sets.get(i).serializeToStream(abstractSerializedData);
        }
    }
}
