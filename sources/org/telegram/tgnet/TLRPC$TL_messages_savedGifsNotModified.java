package org.telegram.tgnet;

public class TLRPC$TL_messages_savedGifsNotModified extends TLRPC$messages_SavedGifs {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
