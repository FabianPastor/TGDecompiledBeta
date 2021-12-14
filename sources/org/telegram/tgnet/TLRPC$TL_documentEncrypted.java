package org.telegram.tgnet;

public class TLRPC$TL_documentEncrypted extends TLRPC$Document {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.id = abstractSerializedData.readInt64(z);
        this.access_hash = abstractSerializedData.readInt64(z);
        this.date = abstractSerializedData.readInt32(z);
        this.mime_type = abstractSerializedData.readString(z);
        this.size = abstractSerializedData.readInt32(z);
        this.thumbs.add(TLRPC$PhotoSize.TLdeserialize(0, 0, 0, abstractSerializedData, abstractSerializedData.readInt32(z), z));
        this.dc_id = abstractSerializedData.readInt32(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            while (i < readInt322) {
                TLRPC$DocumentAttribute TLdeserialize = TLRPC$DocumentAttribute.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.attributes.add(TLdeserialize);
                    i++;
                } else {
                    return;
                }
            }
            this.key = abstractSerializedData.readByteArray(z);
            this.iv = abstractSerializedData.readByteArray(z);
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.id);
        abstractSerializedData.writeInt64(this.access_hash);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeString(this.mime_type);
        abstractSerializedData.writeInt32(this.size);
        this.thumbs.get(0).serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.dc_id);
        abstractSerializedData.writeInt32(NUM);
        int size = this.attributes.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.attributes.get(i).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeByteArray(this.key);
        abstractSerializedData.writeByteArray(this.iv);
    }
}
