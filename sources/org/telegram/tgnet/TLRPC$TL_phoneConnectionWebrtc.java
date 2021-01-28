package org.telegram.tgnet;

public class TLRPC$TL_phoneConnectionWebrtc extends TLRPC$PhoneConnection {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.turn = (readInt32 & 1) != 0;
        if ((readInt32 & 2) != 0) {
            z2 = true;
        }
        this.stun = z2;
        this.id = abstractSerializedData.readInt64(z);
        this.ip = abstractSerializedData.readString(z);
        this.ipv6 = abstractSerializedData.readString(z);
        this.port = abstractSerializedData.readInt32(z);
        this.username = abstractSerializedData.readString(z);
        this.password = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.turn ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.stun ? i | 2 : i & -3;
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        abstractSerializedData.writeInt64(this.id);
        abstractSerializedData.writeString(this.ip);
        abstractSerializedData.writeString(this.ipv6);
        abstractSerializedData.writeInt32(this.port);
        abstractSerializedData.writeString(this.username);
        abstractSerializedData.writeString(this.password);
    }
}
