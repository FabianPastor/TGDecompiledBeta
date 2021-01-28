package org.telegram.tgnet;

public class TLRPC$TL_encryptedFileEmpty extends TLRPC$EncryptedFile {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
