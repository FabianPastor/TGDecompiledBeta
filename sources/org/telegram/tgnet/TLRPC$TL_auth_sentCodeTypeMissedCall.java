package org.telegram.tgnet;

public class TLRPC$TL_auth_sentCodeTypeMissedCall extends TLRPC$auth_SentCodeType {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.prefix = abstractSerializedData.readString(z);
        this.length = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.prefix);
        abstractSerializedData.writeInt32(this.length);
    }
}
