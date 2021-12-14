package org.telegram.tgnet;

public class TLRPC$TL_messageMediaUnsupported_old extends TLRPC$TL_messageMediaUnsupported {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.bytes = abstractSerializedData.readByteArray(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeByteArray(this.bytes);
    }
}
