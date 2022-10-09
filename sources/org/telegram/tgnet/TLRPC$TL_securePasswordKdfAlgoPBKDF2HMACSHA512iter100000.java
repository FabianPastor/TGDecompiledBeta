package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 extends TLRPC$SecurePasswordKdfAlgo {
    public static int constructor = -NUM;
    public byte[] salt;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.salt = abstractSerializedData.readByteArray(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeByteArray(this.salt);
    }
}
