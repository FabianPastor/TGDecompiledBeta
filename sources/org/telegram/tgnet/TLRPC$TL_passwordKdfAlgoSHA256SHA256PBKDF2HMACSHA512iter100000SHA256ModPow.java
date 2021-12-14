package org.telegram.tgnet;

public class TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow extends TLRPC$PasswordKdfAlgo {
    public static int constructor = NUM;
    public int g;
    public byte[] p;
    public byte[] salt1;
    public byte[] salt2;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.salt1 = abstractSerializedData.readByteArray(z);
        this.salt2 = abstractSerializedData.readByteArray(z);
        this.g = abstractSerializedData.readInt32(z);
        this.p = abstractSerializedData.readByteArray(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeByteArray(this.salt1);
        abstractSerializedData.writeByteArray(this.salt2);
        abstractSerializedData.writeInt32(this.g);
        abstractSerializedData.writeByteArray(this.p);
    }
}
