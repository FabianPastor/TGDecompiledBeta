package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$StickerSetCovered extends TLObject {
    public TLRPC$Document cover;
    public ArrayList<TLRPC$Document> covers = new ArrayList<>();
    public TLRPC$StickerSet set;

    public static TLRPC$StickerSetCovered TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$StickerSetCovered tLRPC$TL_stickerSetFullCovered;
        if (i == NUM) {
            tLRPC$TL_stickerSetFullCovered = new TLRPC$TL_stickerSetFullCovered();
        } else if (i == NUM) {
            tLRPC$TL_stickerSetFullCovered = new TLRPC$StickerSetCovered() { // from class: org.telegram.tgnet.TLRPC$TL_stickerSetMultiCovered
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.set = TLRPC$StickerSet.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                    int readInt32 = abstractSerializedData2.readInt32(z2);
                    if (readInt32 != NUM) {
                        if (z2) {
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                        }
                        return;
                    }
                    int readInt322 = abstractSerializedData2.readInt32(z2);
                    for (int i2 = 0; i2 < readInt322; i2++) {
                        TLRPC$Document TLdeserialize = TLRPC$Document.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if (TLdeserialize == null) {
                            return;
                        }
                        this.covers.add(TLdeserialize);
                    }
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    this.set.serializeToStream(abstractSerializedData2);
                    abstractSerializedData2.writeInt32(NUM);
                    int size = this.covers.size();
                    abstractSerializedData2.writeInt32(size);
                    for (int i2 = 0; i2 < size; i2++) {
                        this.covers.get(i2).serializeToStream(abstractSerializedData2);
                    }
                }
            };
        } else {
            tLRPC$TL_stickerSetFullCovered = i != NUM ? null : new TLRPC$StickerSetCovered() { // from class: org.telegram.tgnet.TLRPC$TL_stickerSetCovered
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.set = TLRPC$StickerSet.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                    this.cover = TLRPC$Document.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    this.set.serializeToStream(abstractSerializedData2);
                    this.cover.serializeToStream(abstractSerializedData2);
                }
            };
        }
        if (tLRPC$TL_stickerSetFullCovered != null || !z) {
            if (tLRPC$TL_stickerSetFullCovered != null) {
                tLRPC$TL_stickerSetFullCovered.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_stickerSetFullCovered;
        }
        throw new RuntimeException(String.format("can't parse magic %x in StickerSetCovered", Integer.valueOf(i)));
    }
}
