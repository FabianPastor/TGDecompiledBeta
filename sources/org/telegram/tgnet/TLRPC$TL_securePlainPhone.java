package org.telegram.tgnet;

public class TLRPC$TL_securePlainPhone extends TLRPC$SecurePlainData {
    public static int constructor = NUM;
    public String phone;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.phone = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.phone);
    }
}
