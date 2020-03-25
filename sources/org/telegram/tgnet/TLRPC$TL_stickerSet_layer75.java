package org.telegram.tgnet;

public class TLRPC$TL_stickerSet_layer75 extends TLRPC$TL_stickerSet {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        this.installed = (readInt32 & 1) != 0;
        this.archived = (this.flags & 2) != 0;
        this.official = (this.flags & 4) != 0;
        if ((this.flags & 8) == 0) {
            z2 = false;
        }
        this.masks = z2;
        this.id = abstractSerializedData.readInt64(z);
        this.access_hash = abstractSerializedData.readInt64(z);
        this.title = abstractSerializedData.readString(z);
        this.short_name = abstractSerializedData.readString(z);
        this.count = abstractSerializedData.readInt32(z);
        this.hash = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.installed ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.archived ? i | 2 : i & -3;
        this.flags = i2;
        int i3 = this.official ? i2 | 4 : i2 & -5;
        this.flags = i3;
        int i4 = this.masks ? i3 | 8 : i3 & -9;
        this.flags = i4;
        abstractSerializedData.writeInt32(i4);
        abstractSerializedData.writeInt64(this.id);
        abstractSerializedData.writeInt64(this.access_hash);
        abstractSerializedData.writeString(this.title);
        abstractSerializedData.writeString(this.short_name);
        abstractSerializedData.writeInt32(this.count);
        abstractSerializedData.writeInt32(this.hash);
    }
}
