package org.telegram.tgnet;

public class TLRPC$TL_securePasswordKdfAlgoSHA512 extends TLRPC$SecurePasswordKdfAlgo {
    public static int constructor = -NUM;
    public byte[] salt;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.salt = abstractSerializedData.readByteArray(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeByteArray(this.salt);
    }
}
