package org.telegram.tgnet;

public class TLRPC$TL_auth_sentCodeTypeFlashCall extends TLRPC$auth_SentCodeType {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.pattern = abstractSerializedData.readString(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.pattern);
    }
}
