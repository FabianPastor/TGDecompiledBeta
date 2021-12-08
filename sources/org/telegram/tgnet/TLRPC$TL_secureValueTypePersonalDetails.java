package org.telegram.tgnet;

public class TLRPC$TL_secureValueTypePersonalDetails extends TLRPC$SecureValueType {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
