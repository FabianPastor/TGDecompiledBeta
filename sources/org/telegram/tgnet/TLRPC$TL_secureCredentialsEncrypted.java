package org.telegram.tgnet;

public class TLRPC$TL_secureCredentialsEncrypted extends TLObject {
    public static int constructor = NUM;
    public byte[] data;
    public byte[] hash;
    public byte[] secret;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.data = abstractSerializedData.readByteArray(z);
        this.hash = abstractSerializedData.readByteArray(z);
        this.secret = abstractSerializedData.readByteArray(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeByteArray(this.data);
        abstractSerializedData.writeByteArray(this.hash);
        abstractSerializedData.writeByteArray(this.secret);
    }
}
