package org.telegram.tgnet;

public class TLRPC$TL_account_chatThemesNotModified extends TLRPC$account_ChatThemes {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
