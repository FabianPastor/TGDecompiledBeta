package org.telegram.tgnet;

public class TLRPC$TL_channelForbidden_layer131 extends TLRPC$TL_channelForbidden {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        this.broadcast = (readInt32 & 32) != 0;
        if ((readInt32 & 256) == 0) {
            z2 = false;
        }
        this.megagroup = z2;
        this.id = (long) abstractSerializedData.readInt32(z);
        this.access_hash = abstractSerializedData.readInt64(z);
        this.title = abstractSerializedData.readString(z);
        if ((this.flags & 65536) != 0) {
            this.until_date = abstractSerializedData.readInt32(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.broadcast ? this.flags | 32 : this.flags & -33;
        this.flags = i;
        int i2 = this.megagroup ? i | 256 : i & -257;
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        abstractSerializedData.writeInt32((int) this.id);
        abstractSerializedData.writeInt64(this.access_hash);
        abstractSerializedData.writeString(this.title);
        if ((this.flags & 65536) != 0) {
            abstractSerializedData.writeInt32(this.until_date);
        }
    }
}
