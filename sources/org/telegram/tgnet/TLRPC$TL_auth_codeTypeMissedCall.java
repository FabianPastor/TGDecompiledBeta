package org.telegram.tgnet;

public class TLRPC$TL_auth_codeTypeMissedCall extends TLRPC$auth_CodeType {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
