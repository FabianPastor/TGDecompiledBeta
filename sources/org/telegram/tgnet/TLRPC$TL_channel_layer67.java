package org.telegram.tgnet;

public class TLRPC$TL_channel_layer67 extends TLRPC$TL_channel {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.creator = (readInt32 & 1) != 0;
        this.kicked = (readInt32 & 2) != 0;
        this.left = (readInt32 & 4) != 0;
        this.moderator = (readInt32 & 16) != 0;
        this.broadcast = (readInt32 & 32) != 0;
        this.verified = (readInt32 & 128) != 0;
        this.megagroup = (readInt32 & 256) != 0;
        this.restricted = (readInt32 & 512) != 0;
        this.signatures = (readInt32 & 2048) != 0;
        if ((readInt32 & 4096) != 0) {
            z2 = true;
        }
        this.min = z2;
        this.id = (long) abstractSerializedData.readInt32(z);
        if ((this.flags & 8192) != 0) {
            this.access_hash = abstractSerializedData.readInt64(z);
        }
        this.title = abstractSerializedData.readString(z);
        if ((this.flags & 64) != 0) {
            this.username = abstractSerializedData.readString(z);
        }
        this.photo = TLRPC$ChatPhoto.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.date = abstractSerializedData.readInt32(z);
        this.version = abstractSerializedData.readInt32(z);
        if ((this.flags & 512) != 0) {
            abstractSerializedData.readString(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.creator ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.kicked ? i | 2 : i & -3;
        this.flags = i2;
        int i3 = this.left ? i2 | 4 : i2 & -5;
        this.flags = i3;
        int i4 = this.moderator ? i3 | 16 : i3 & -17;
        this.flags = i4;
        int i5 = this.broadcast ? i4 | 32 : i4 & -33;
        this.flags = i5;
        int i6 = this.verified ? i5 | 128 : i5 & -129;
        this.flags = i6;
        int i7 = this.megagroup ? i6 | 256 : i6 & -257;
        this.flags = i7;
        int i8 = this.restricted ? i7 | 512 : i7 & -513;
        this.flags = i8;
        int i9 = this.signatures ? i8 | 2048 : i8 & -2049;
        this.flags = i9;
        int i10 = this.min ? i9 | 4096 : i9 & -4097;
        this.flags = i10;
        abstractSerializedData.writeInt32(i10);
        abstractSerializedData.writeInt32((int) this.id);
        if ((this.flags & 8192) != 0) {
            abstractSerializedData.writeInt64(this.access_hash);
        }
        abstractSerializedData.writeString(this.title);
        if ((this.flags & 64) != 0) {
            abstractSerializedData.writeString(this.username);
        }
        this.photo.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeInt32(this.version);
        if ((this.flags & 512) != 0) {
            abstractSerializedData.writeString("");
        }
    }
}
