package org.telegram.tgnet;

public class TLRPC$TL_channel_layer48 extends TLRPC$TL_channel {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        this.creator = (readInt32 & 1) != 0;
        this.kicked = (this.flags & 2) != 0;
        this.left = (this.flags & 4) != 0;
        this.moderator = (this.flags & 16) != 0;
        this.broadcast = (this.flags & 32) != 0;
        this.verified = (this.flags & 128) != 0;
        this.megagroup = (this.flags & 256) != 0;
        this.restricted = (this.flags & 512) != 0;
        if ((this.flags & 2048) == 0) {
            z2 = false;
        }
        this.signatures = z2;
        this.id = abstractSerializedData.readInt32(z);
        this.access_hash = abstractSerializedData.readInt64(z);
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
        abstractSerializedData.writeInt32(i9);
        abstractSerializedData.writeInt32(this.id);
        abstractSerializedData.writeInt64(this.access_hash);
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
