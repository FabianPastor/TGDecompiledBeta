package org.telegram.tgnet;

public class TLRPC$TL_inputMediaUploadedPhoto extends TLRPC$InputMedia {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.file = TLRPC$InputFile.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        if ((this.flags & 1) != 0) {
            int readInt32 = abstractSerializedData.readInt32(z);
            int i = 0;
            if (readInt32 == NUM) {
                int readInt322 = abstractSerializedData.readInt32(z);
                while (i < readInt322) {
                    TLRPC$InputDocument TLdeserialize = TLRPC$InputDocument.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize != null) {
                        this.stickers.add(TLdeserialize);
                        i++;
                    } else {
                        return;
                    }
                }
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
            } else {
                return;
            }
        }
        if ((this.flags & 2) != 0) {
            this.ttl_seconds = abstractSerializedData.readInt32(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        this.file.serializeToStream(abstractSerializedData);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(NUM);
            int size = this.stickers.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.stickers.get(i).serializeToStream(abstractSerializedData);
            }
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(this.ttl_seconds);
        }
    }
}
