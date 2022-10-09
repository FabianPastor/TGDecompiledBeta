package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_page extends TLRPC$Page {
    public static int constructor = -NUM;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        this.part = (readInt32 & 1) != 0;
        this.rtl = (readInt32 & 2) != 0;
        this.v2 = (readInt32 & 4) != 0;
        this.url = abstractSerializedData.readString(z);
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 != NUM) {
            if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
            }
            return;
        }
        int readInt323 = abstractSerializedData.readInt32(z);
        for (int i = 0; i < readInt323; i++) {
            TLRPC$PageBlock TLdeserialize = TLRPC$PageBlock.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if (TLdeserialize == null) {
                return;
            }
            this.blocks.add(TLdeserialize);
        }
        int readInt324 = abstractSerializedData.readInt32(z);
        if (readInt324 != NUM) {
            if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt324)));
            }
            return;
        }
        int readInt325 = abstractSerializedData.readInt32(z);
        for (int i2 = 0; i2 < readInt325; i2++) {
            TLRPC$Photo TLdeserialize2 = TLRPC$Photo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if (TLdeserialize2 == null) {
                return;
            }
            this.photos.add(TLdeserialize2);
        }
        int readInt326 = abstractSerializedData.readInt32(z);
        if (readInt326 != NUM) {
            if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt326)));
            }
            return;
        }
        int readInt327 = abstractSerializedData.readInt32(z);
        for (int i3 = 0; i3 < readInt327; i3++) {
            TLRPC$Document TLdeserialize3 = TLRPC$Document.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if (TLdeserialize3 == null) {
                return;
            }
            this.documents.add(TLdeserialize3);
        }
        if ((this.flags & 8) == 0) {
            return;
        }
        this.views = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.part ? this.flags | 1 : this.flags & (-2);
        this.flags = i;
        int i2 = this.rtl ? i | 2 : i & (-3);
        this.flags = i2;
        int i3 = this.v2 ? i2 | 4 : i2 & (-5);
        this.flags = i3;
        abstractSerializedData.writeInt32(i3);
        abstractSerializedData.writeString(this.url);
        abstractSerializedData.writeInt32(NUM);
        int size = this.blocks.size();
        abstractSerializedData.writeInt32(size);
        for (int i4 = 0; i4 < size; i4++) {
            this.blocks.get(i4).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(NUM);
        int size2 = this.photos.size();
        abstractSerializedData.writeInt32(size2);
        for (int i5 = 0; i5 < size2; i5++) {
            this.photos.get(i5).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(NUM);
        int size3 = this.documents.size();
        abstractSerializedData.writeInt32(size3);
        for (int i6 = 0; i6 < size3; i6++) {
            this.documents.get(i6).serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 8) != 0) {
            abstractSerializedData.writeInt32(this.views);
        }
    }
}
