package org.telegram.tgnet;

public class TLRPC$TL_page extends TLRPC$Page {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.part = (readInt32 & 1) != 0;
        this.rtl = (this.flags & 2) != 0;
        this.v2 = (this.flags & 4) != 0;
        this.url = abstractSerializedData.readString(z);
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 == NUM) {
            int readInt323 = abstractSerializedData.readInt32(z);
            int i2 = 0;
            while (i2 < readInt323) {
                TLRPC$PageBlock TLdeserialize = TLRPC$PageBlock.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.blocks.add(TLdeserialize);
                    i2++;
                } else {
                    return;
                }
            }
            int readInt324 = abstractSerializedData.readInt32(z);
            if (readInt324 == NUM) {
                int readInt325 = abstractSerializedData.readInt32(z);
                int i3 = 0;
                while (i3 < readInt325) {
                    TLRPC$Photo TLdeserialize2 = TLRPC$Photo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize2 != null) {
                        this.photos.add(TLdeserialize2);
                        i3++;
                    } else {
                        return;
                    }
                }
                int readInt326 = abstractSerializedData.readInt32(z);
                if (readInt326 == NUM) {
                    int readInt327 = abstractSerializedData.readInt32(z);
                    while (i < readInt327) {
                        TLRPC$Document TLdeserialize3 = TLRPC$Document.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                        if (TLdeserialize3 != null) {
                            this.documents.add(TLdeserialize3);
                            i++;
                        } else {
                            return;
                        }
                    }
                    if ((this.flags & 8) != 0) {
                        this.views = abstractSerializedData.readInt32(z);
                    }
                } else if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt326)}));
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt324)}));
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.part ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.rtl ? i | 2 : i & -3;
        this.flags = i2;
        int i3 = this.v2 ? i2 | 4 : i2 & -5;
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
