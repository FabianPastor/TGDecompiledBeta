package org.telegram.tgnet;

public class TLRPC$TL_inputMediaUploadedDocument extends TLRPC$InputMedia {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.nosound_video = (readInt32 & 8) != 0;
        this.file = TLRPC$InputFile.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        if ((this.flags & 4) != 0) {
            this.thumb = TLRPC$InputFile.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        this.mime_type = abstractSerializedData.readString(z);
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 == NUM) {
            int readInt323 = abstractSerializedData.readInt32(z);
            int i2 = 0;
            while (i2 < readInt323) {
                TLRPC$DocumentAttribute TLdeserialize = TLRPC$DocumentAttribute.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.attributes.add(TLdeserialize);
                    i2++;
                } else {
                    return;
                }
            }
            if ((this.flags & 1) != 0) {
                int readInt324 = abstractSerializedData.readInt32(z);
                if (readInt324 == NUM) {
                    int readInt325 = abstractSerializedData.readInt32(z);
                    while (i < readInt325) {
                        TLRPC$InputDocument TLdeserialize2 = TLRPC$InputDocument.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                        if (TLdeserialize2 != null) {
                            this.stickers.add(TLdeserialize2);
                            i++;
                        } else {
                            return;
                        }
                    }
                } else if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt324)}));
                } else {
                    return;
                }
            }
            if ((this.flags & 2) != 0) {
                this.ttl_seconds = abstractSerializedData.readInt32(z);
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.nosound_video ? this.flags | 8 : this.flags & -9;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        this.file.serializeToStream(abstractSerializedData);
        if ((this.flags & 4) != 0) {
            this.thumb.serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeString(this.mime_type);
        abstractSerializedData.writeInt32(NUM);
        int size = this.attributes.size();
        abstractSerializedData.writeInt32(size);
        for (int i2 = 0; i2 < size; i2++) {
            this.attributes.get(i2).serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(NUM);
            int size2 = this.stickers.size();
            abstractSerializedData.writeInt32(size2);
            for (int i3 = 0; i3 < size2; i3++) {
                this.stickers.get(i3).serializeToStream(abstractSerializedData);
            }
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(this.ttl_seconds);
        }
    }
}
