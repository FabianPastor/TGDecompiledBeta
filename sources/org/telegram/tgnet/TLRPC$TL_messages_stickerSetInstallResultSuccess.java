package org.telegram.tgnet;

public class TLRPC$TL_messages_stickerSetInstallResultSuccess extends TLRPC$messages_StickerSetInstallResult {
    public static int constructor = NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
