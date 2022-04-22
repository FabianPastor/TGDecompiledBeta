package org.telegram.tgnet;

public class TLRPC$TL_passwordKdfAlgoUnknown extends TLRPC$PasswordKdfAlgo {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
