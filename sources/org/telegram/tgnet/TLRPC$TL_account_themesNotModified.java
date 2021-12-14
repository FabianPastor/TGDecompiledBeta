package org.telegram.tgnet;

public class TLRPC$TL_account_themesNotModified extends TLRPC$account_Themes {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
