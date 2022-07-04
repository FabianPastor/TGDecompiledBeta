package org.telegram.tgnet;

public class TLRPC$TL_document extends TLRPC$Document {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        boolean z2 = z;
        this.flags = abstractSerializedData.readInt32(z);
        this.id = abstractSerializedData.readInt64(z);
        this.access_hash = abstractSerializedData.readInt64(z);
        this.file_reference = abstractSerializedData.readByteArray(z);
        this.date = abstractSerializedData.readInt32(z);
        this.mime_type = abstractSerializedData.readString(z);
        this.size = abstractSerializedData.readInt64(z);
        int i = 0;
        if ((this.flags & 1) != 0) {
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 == NUM) {
                int readInt322 = abstractSerializedData.readInt32(z);
                int i2 = 0;
                while (i2 < readInt322) {
                    int i3 = i2;
                    TLRPC$PhotoSize TLdeserialize = TLRPC$PhotoSize.TLdeserialize(0, this.id, 0, abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize != null) {
                        this.thumbs.add(TLdeserialize);
                        i2 = i3 + 1;
                    } else {
                        return;
                    }
                }
            } else if (z2) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
            } else {
                return;
            }
        }
        if ((this.flags & 2) != 0) {
            int readInt323 = abstractSerializedData.readInt32(z);
            if (readInt323 == NUM) {
                int readInt324 = abstractSerializedData.readInt32(z);
                int i4 = 0;
                while (i4 < readInt324) {
                    TLRPC$VideoSize TLdeserialize2 = TLRPC$VideoSize.TLdeserialize(0, this.id, abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize2 != null) {
                        this.video_thumbs.add(TLdeserialize2);
                        i4++;
                    } else {
                        return;
                    }
                }
            } else if (z2) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt323)}));
            } else {
                return;
            }
        }
        this.dc_id = abstractSerializedData.readInt32(z);
        int readInt325 = abstractSerializedData.readInt32(z);
        if (readInt325 == NUM) {
            int readInt326 = abstractSerializedData.readInt32(z);
            while (i < readInt326) {
                TLRPC$DocumentAttribute TLdeserialize3 = TLRPC$DocumentAttribute.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z2);
                if (TLdeserialize3 != null) {
                    this.attributes.add(TLdeserialize3);
                    i++;
                } else {
                    return;
                }
            }
        } else if (z2) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt325)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeInt64(this.id);
        abstractSerializedData.writeInt64(this.access_hash);
        abstractSerializedData.writeByteArray(this.file_reference);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeString(this.mime_type);
        abstractSerializedData.writeInt64(this.size);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(NUM);
            int size = this.thumbs.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.thumbs.get(i).serializeToStream(abstractSerializedData);
            }
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(NUM);
            int size2 = this.video_thumbs.size();
            abstractSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                this.video_thumbs.get(i2).serializeToStream(abstractSerializedData);
            }
        }
        abstractSerializedData.writeInt32(this.dc_id);
        abstractSerializedData.writeInt32(NUM);
        int size3 = this.attributes.size();
        abstractSerializedData.writeInt32(size3);
        for (int i3 = 0; i3 < size3; i3++) {
            this.attributes.get(i3).serializeToStream(abstractSerializedData);
        }
    }
}
