package org.telegram.tgnet;

public class TLRPC$TL_auth_sentCodeTypeSms extends TLRPC$auth_SentCodeType {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.length = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.length);
    }
}
