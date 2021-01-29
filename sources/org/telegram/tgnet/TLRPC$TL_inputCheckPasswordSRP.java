package org.telegram.tgnet;

public class TLRPC$TL_inputCheckPasswordSRP extends TLRPC$InputCheckPasswordSRP {
    public static int constructor = -NUM;
    public byte[] A;
    public byte[] M1;
    public long srp_id;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.srp_id = abstractSerializedData.readInt64(z);
        this.A = abstractSerializedData.readByteArray(z);
        this.M1 = abstractSerializedData.readByteArray(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.srp_id);
        abstractSerializedData.writeByteArray(this.A);
        abstractSerializedData.writeByteArray(this.M1);
    }
}
