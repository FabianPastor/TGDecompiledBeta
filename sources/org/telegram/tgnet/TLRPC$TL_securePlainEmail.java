package org.telegram.tgnet;

public class TLRPC$TL_securePlainEmail extends TLRPC$SecurePlainData {
    public static int constructor = NUM;
    public String email;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.email = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.email);
    }
}
