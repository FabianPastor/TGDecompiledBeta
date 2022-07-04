package org.telegram.tgnet;

public class TLRPC$TL_phoneConnection extends TLRPC$PhoneConnection {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        if ((readInt32 & 1) == 0) {
            z2 = false;
        }
        this.tcp = z2;
        this.id = abstractSerializedData.readInt64(z);
        this.ip = abstractSerializedData.readString(z);
        this.ipv6 = abstractSerializedData.readString(z);
        this.port = abstractSerializedData.readInt32(z);
        this.peer_tag = abstractSerializedData.readByteArray(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.tcp ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        abstractSerializedData.writeInt64(this.id);
        abstractSerializedData.writeString(this.ip);
        abstractSerializedData.writeString(this.ipv6);
        abstractSerializedData.writeInt32(this.port);
        abstractSerializedData.writeByteArray(this.peer_tag);
    }
}
