package org.telegram.tgnet;

public class TLRPC$TL_inputPrivacyKeyForwards extends TLRPC$InputPrivacyKey {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
