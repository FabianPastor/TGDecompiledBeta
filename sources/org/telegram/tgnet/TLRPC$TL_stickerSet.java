package org.telegram.tgnet;

public class TLRPC$TL_stickerSet extends TLRPC$StickerSet {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.archived = (readInt32 & 2) != 0;
        this.official = (this.flags & 4) != 0;
        this.masks = (this.flags & 8) != 0;
        if ((this.flags & 32) != 0) {
            z2 = true;
        }
        this.animated = z2;
        if ((this.flags & 1) != 0) {
            this.installed_date = abstractSerializedData.readInt32(z);
        }
        this.id = abstractSerializedData.readInt64(z);
        this.access_hash = abstractSerializedData.readInt64(z);
        this.title = abstractSerializedData.readString(z);
        this.short_name = abstractSerializedData.readString(z);
        if ((this.flags & 16) != 0) {
            this.thumb = TLRPC$PhotoSize.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 16) != 0) {
            this.thumb_dc_id = abstractSerializedData.readInt32(z);
        }
        this.count = abstractSerializedData.readInt32(z);
        this.hash = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.archived ? this.flags | 2 : this.flags & -3;
        this.flags = i;
        int i2 = this.official ? i | 4 : i & -5;
        this.flags = i2;
        int i3 = this.masks ? i2 | 8 : i2 & -9;
        this.flags = i3;
        int i4 = this.animated ? i3 | 32 : i3 & -33;
        this.flags = i4;
        abstractSerializedData.writeInt32(i4);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.installed_date);
        }
        abstractSerializedData.writeInt64(this.id);
        abstractSerializedData.writeInt64(this.access_hash);
        abstractSerializedData.writeString(this.title);
        abstractSerializedData.writeString(this.short_name);
        if ((this.flags & 16) != 0) {
            this.thumb.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 16) != 0) {
            abstractSerializedData.writeInt32(this.thumb_dc_id);
        }
        abstractSerializedData.writeInt32(this.count);
        abstractSerializedData.writeInt32(this.hash);
    }
}