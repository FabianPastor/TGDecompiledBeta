package org.telegram.tgnet;

public class TLRPC$TL_photo_layer115 extends TLRPC$TL_photo {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.has_stickers = (readInt32 & 1) != 0;
        this.id = abstractSerializedData.readInt64(z);
        this.access_hash = abstractSerializedData.readInt64(z);
        this.file_reference = abstractSerializedData.readByteArray(z);
        this.date = abstractSerializedData.readInt32(z);
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 == NUM) {
            int readInt323 = abstractSerializedData.readInt32(z);
            while (i < readInt323) {
                TLRPC$PhotoSize TLdeserialize = TLRPC$PhotoSize.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.sizes.add(TLdeserialize);
                    i++;
                } else {
                    return;
                }
            }
            this.dc_id = abstractSerializedData.readInt32(z);
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.has_stickers ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        abstractSerializedData.writeInt64(this.id);
        abstractSerializedData.writeInt64(this.access_hash);
        abstractSerializedData.writeByteArray(this.file_reference);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeInt32(NUM);
        int size = this.sizes.size();
        abstractSerializedData.writeInt32(size);
        for (int i2 = 0; i2 < size; i2++) {
            this.sizes.get(i2).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(this.dc_id);
    }
}
