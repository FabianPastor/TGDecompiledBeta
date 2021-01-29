package org.telegram.tgnet;

public class TLRPC$TL_decryptedMessageMediaEmpty extends TLRPC$DecryptedMessageMedia {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
