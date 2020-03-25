package org.telegram.tgnet;

public class TLRPC$TL_chat_old2 extends TLRPC$TL_chat {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        this.creator = (readInt32 & 1) != 0;
        this.kicked = (this.flags & 2) != 0;
        this.left = (this.flags & 4) != 0;
        if ((this.flags & 32) == 0) {
            z2 = false;
        }
        this.deactivated = z2;
        this.id = abstractSerializedData.readInt32(z);
        this.title = abstractSerializedData.readString(z);
        this.photo = TLRPC$ChatPhoto.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.participants_count = abstractSerializedData.readInt32(z);
        this.date = abstractSerializedData.readInt32(z);
        this.version = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.creator ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.kicked ? i | 2 : i & -3;
        this.flags = i2;
        int i3 = this.left ? i2 | 4 : i2 & -5;
        this.flags = i3;
        int i4 = this.deactivated ? i3 | 32 : i3 & -33;
        this.flags = i4;
        abstractSerializedData.writeInt32(i4);
        abstractSerializedData.writeInt32(this.id);
        abstractSerializedData.writeString(this.title);
        this.photo.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.participants_count);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeInt32(this.version);
    }
}
