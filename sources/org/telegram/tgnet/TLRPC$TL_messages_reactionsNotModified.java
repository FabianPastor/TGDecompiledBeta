package org.telegram.tgnet;

public class TLRPC$TL_messages_reactionsNotModified extends TLRPC$messages_Reactions {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
