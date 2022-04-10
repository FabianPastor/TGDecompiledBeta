package org.telegram.tgnet;

public class TLRPC$TL_attachMenuBotsNotModified extends TLRPC$AttachMenuBots {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
