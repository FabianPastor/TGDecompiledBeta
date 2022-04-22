package org.telegram.tgnet;

public class TLRPC$TL_privacyValueAllowAll extends TLRPC$PrivacyRule {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
