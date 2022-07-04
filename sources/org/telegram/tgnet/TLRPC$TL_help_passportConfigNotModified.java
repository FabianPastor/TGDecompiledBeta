package org.telegram.tgnet;

public class TLRPC$TL_help_passportConfigNotModified extends TLRPC$help_PassportConfig {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
