package org.telegram.tgnet;

public class TLRPC$TL_phoneConnection extends TLRPC$PhoneConnection {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.id = abstractSerializedData.readInt64(z);
        this.ip = abstractSerializedData.readString(z);
        this.ipv6 = abstractSerializedData.readString(z);
        this.port = abstractSerializedData.readInt32(z);
        this.peer_tag = abstractSerializedData.readByteArray(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.id);
        abstractSerializedData.writeString(this.ip);
        abstractSerializedData.writeString(this.ipv6);
        abstractSerializedData.writeInt32(this.port);
        abstractSerializedData.writeByteArray(this.peer_tag);
    }
}
