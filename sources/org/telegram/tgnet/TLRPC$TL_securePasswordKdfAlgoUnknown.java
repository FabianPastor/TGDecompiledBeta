package org.telegram.tgnet;

public class TLRPC$TL_securePasswordKdfAlgoUnknown extends TLRPC$SecurePasswordKdfAlgo {
    public static int constructor = 4883767;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
