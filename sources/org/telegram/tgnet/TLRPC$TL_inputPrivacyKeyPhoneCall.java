package org.telegram.tgnet;

public class TLRPC$TL_inputPrivacyKeyPhoneCall extends TLRPC$InputPrivacyKey {
    public static int constructor = -88417185;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
