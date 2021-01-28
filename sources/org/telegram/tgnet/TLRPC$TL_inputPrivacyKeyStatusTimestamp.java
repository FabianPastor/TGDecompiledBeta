package org.telegram.tgnet;

public class TLRPC$TL_inputPrivacyKeyStatusTimestamp extends TLRPC$InputPrivacyKey {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
