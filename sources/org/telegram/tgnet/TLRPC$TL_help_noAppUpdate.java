package org.telegram.tgnet;

public class TLRPC$TL_help_noAppUpdate extends TLRPC$help_AppUpdate {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
